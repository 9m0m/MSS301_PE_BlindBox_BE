# HƯỚNG DẪN THAY ĐỔI CẤU TRÚC DỰ ÁN - MICROSERVICES BLIND BOX

## 📋 MỤC LỤC
1. [Tổng quan kiến trúc hiện tại](#1-tổng-quan-kiến-trúc-hiện-tại)
2. [Chi tiết từng microservice](#2-chi-tiết-từng-microservice)
3. [Phân tích điểm thay đổi](#3-phân-tích-điểm-thay-đổi)
4. [Hướng dẫn thay đổi từng loại](#4-hướng-dẫn-thay-đổi-từng-loại)
5. [Checklist khi thay đổi](#5-checklist-khi-thay-đổi)

---

## 1. TỔNG QUAN KIẾN TRÚC HIỆN TẠI

### 1.1. Sơ đồ Microservices
```
                    ┌─────────────────────┐
                    │   API Gateway       │
                    │   Port: 8080        │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
    ┌─────────▼────────┐ ┌────▼────────┐ ┌────▼────────┐
    │  MSAccount       │ │ MSBlindBox  │ │  MSBrand    │
    │  Port: 8081      │ │ Port: 8082  │ │  Port: 8083 │
    └──────────────────┘ └──────┬──────┘ └─────▲───────┘
                                │                │
                                │  RestTemplate  │
                                └────────────────┘
```

### 1.2. Database Structure
- **MSS301Summer25DBAccount**: Chứa SystemAccounts
- **MSS301Summer25DBBlindBox**: Chứa BlindBoxes, BlindBoxCategories
- **MSS301Summer25DBBrand**: Chứa Brand, BlindBoxes (sync copy)

### 1.3. Package Structure Hiện Tại
```
com.mss301.<service_name>/
├── config/          # SecurityConfig, OpenApiConfig, AppConfig
├── controller/      # REST endpoints
├── dto/            # Data Transfer Objects
├── entity/         # JPA Entities
├── repository/     # Spring Data JPA Repositories
├── service/        # Business logic interfaces
│   └── impl/       # Service implementations
├── util/           # Utilities (JwtUtil, etc.)
└── filter/         # Gateway filters (chỉ có ở Gateway)
```

---

## 2. CHI TIẾT TỪNG MICROSERVICE

### 2.1. API GATEWAY (Port 8080)

#### A. Vai trò
- **Routing**: Điều hướng request đến đúng microservice
- **Authentication**: Xác thực JWT token
- **CORS**: Xử lý cross-origin requests
- **Swagger Aggregation**: Tổng hợp API docs từ tất cả services

#### B. Cấu trúc quan trọng

**File: `application.properties`**
```properties
# Routes configuration
spring.cloud.gateway.routes[0].id=msaccount-service
spring.cloud.gateway.routes[0].uri=http://localhost:8081
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/auth/**

spring.cloud.gateway.routes[1].id=msbrand-service
spring.cloud.gateway.routes[1].uri=http://localhost:8083
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/brands/**

spring.cloud.gateway.routes[2].id=msblindbox-service
spring.cloud.gateway.routes[2].uri=http://localhost:8082
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/blindboxes/**,/api/categories/**

spring.cloud.gateway.routes[3].id=msbrand-internal
spring.cloud.gateway.routes[3].uri=http://localhost:8083
spring.cloud.gateway.routes[3].predicates[0]=Path=/api/internal/blindboxes/**
```

**File: `JwtAuthenticationFilter.java`**
- Xác thực JWT token
- Bypass cho các path public: `/api/auth/**`, GET `/api/blindboxes`, swagger paths

**File: `SecurityConfig.java`**
- CORS configuration
- Disable CSRF, form login, http basic
- Permit all cho swagger và auth endpoints

#### C. Dependencies với Services
- **MSAccount (8081)**: Auth endpoints (`/api/auth/**`)
- **MSBlindBox (8082)**: BlindBox endpoints (`/api/blindboxes/**`, `/api/categories/**`)
- **MSBrand (8083)**: Brand endpoints (`/api/brands/**`) và Internal sync (`/api/internal/blindboxes/**`)

---

### 2.2. MS ACCOUNT (Port 8081)

#### A. Entities

**SystemAccount.java**
```java
@Entity
@Table(name = "SystemAccounts")
public class SystemAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Integer accountId;
    
    @Column(name = "Username", nullable = false, length = 100)
    private String username;
    
    @Column(name = "Email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "Password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "Role")
    private Integer role;  // 1 = ADMINISTRATOR, khác = CUSTOMER
    
    @Column(name = "IsActive")
    private Boolean isActive;
}
```

#### B. Repository

**SystemAccountRepository.java**
```java
@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Integer> {
    Optional<SystemAccount> findByEmail(String email);
}
```

#### C. DTOs

**LoginRequest.java**
```java
public class LoginRequest {
    private String email;
    private String password;
}
```

**LoginResponse.java**
```java
public class LoginResponse {
    private String token;
    private Integer accountId;
    private String email;
    private Integer role;
    private Boolean isActive;
}
```

#### D. Controller

**AuthController.java**
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request)
}
```

#### E. Service Logic
- **AuthServiceImpl**: 
  - Tìm account theo email
  - Verify password (plain text comparison)
  - Check isActive
  - Generate JWT token với claims: accountId, email, role, isActive

#### F. Utilities

**JwtUtil.java**
- `generateToken()`: Tạo JWT với claims
- Secret key: `BlindBoxPE2025SecretKeyForJWTTokenGenerationMSS301Summer25Practice`
- Expiration: 86400000ms (24 hours)

---

### 2.3. MS BLIND BOX (Port 8082)

#### A. Entities

**BlindBox.java**
```java
@Entity
@Table(name = "BlindBoxes")
public class BlindBox {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlindBoxID")
    private Integer blindBoxId;
    
    @Column(name = "Name", length = 255)
    private String name;  // PHẢI > 10 characters
    
    @Column(name = "CategoryID")
    private Integer categoryId;
    
    @Column(name = "BrandID")
    private Integer brandId;
    
    @Column(name = "Rarity", length = 50)
    private String rarity;
    
    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;
    
    @Column(name = "Stock")
    private Integer stock;  // PHẢI từ 1-100
}
```

**BlindBoxCategory.java**
```java
@Entity
@Table(name = "BlindBoxCategories")
public class BlindBoxCategory {
    @Id @GeneratedValue
    @Column(name = "CategoryID")
    private Integer categoryId;
    
    @Column(name = "CategoryName")
    private String categoryName;
}
```

#### B. Repository

**BlindBoxRepository.java**
```java
@Repository
public interface BlindBoxRepository extends JpaRepository<BlindBox, Integer> {
    @Query("SELECT new com.mss301.msblindbox_se184531.dto.BlindBoxDTO(" +
            "b.blindBoxId, b.name, b.categoryId, c.categoryName, b.brandId, " +
            "b.rarity, b.price, b.releaseDate, b.stock) " +
            "FROM BlindBox b " +
            "LEFT JOIN BlindBoxCategory c ON c.categoryId = b.categoryId " +
            "ORDER BY b.blindBoxId DESC")
    List<BlindBoxDTO> findAllWithCategoryNameOrderByIdDesc();
}
```

**BlindBoxCategoryRepository.java**
```java
@Repository
public interface BlindBoxCategoryRepository extends JpaRepository<BlindBoxCategory, Integer> {
}
```

#### C. DTOs

**BlindBoxDTO.java**
```java
public class BlindBoxDTO {
    private Integer blindBoxId;
    private String name;
    private Integer categoryId;
    private String categoryName;  // JOIN từ BlindBoxCategory
    private Integer brandId;
    private String rarity;
    private BigDecimal price;
    private LocalDate releaseDate;
    private Integer stock;
}
```

#### D. Controllers

**BlindBoxController.java**
```java
@RestController
@RequestMapping("/api/blindboxes")
public class BlindBoxController {
    @GetMapping              // PUBLIC
    @PostMapping             // ADMIN only
    @PutMapping("/{id}")     // ADMIN only
    @DeleteMapping("/{id}")  // ADMIN only
}
```

#### E. Service Logic

**BlindBoxServiceImpl.java**
- **getAllBlindBoxes()**: Trả về list BlindBoxDTO với category name
- **addBlindBox()**: 
  - Validate (name > 10 chars, stock 1-100)
  - Set releaseDate = now
  - Save vào DB
  - **SYNC qua MSBrand** bằng BrandSyncClient
- **updateBlindBox()**: 
  - Check exists
  - Validate
  - Update releaseDate = now
  - Save vào DB
  - **SYNC qua MSBrand**
- **deleteBlindBox()**:
  - **SYNC DELETE qua MSBrand TRƯỚC**
  - Sau đó mới delete local

#### F. Inter-Service Communication

**BrandSyncClient.java**
```java
@Component
public class BrandSyncClient {
    private final RestTemplate restTemplate;
    private final String msbrandServiceUrl = "http://localhost:8083";
    
    public void createBlindBox(BlindBox blindBox) {
        restTemplate.postForObject(
            msbrandServiceUrl + "/api/internal/blindboxes", 
            blindBox, 
            String.class);
    }
    
    public void updateBlindBox(Integer id, BlindBox blindBox) {
        restTemplate.put(
            msbrandServiceUrl + "/api/internal/blindboxes/" + id, 
            blindBox);
    }
    
    public void deleteBlindBox(Integer id) {
        restTemplate.delete(
            msbrandServiceUrl + "/api/internal/blindboxes/" + id);
    }
}
```

#### G. Configuration

**AppConfig.java**
```java
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

**application.properties**
```properties
server.port=8082
msbrand.service.url=http://localhost:8083

jwt.role-claim=role
jwt.active-claim=isActive
jwt.admin-role-value=1
jwt.admin-authority-name=ROLE_ADMINISTRATOR
jwt.customer-authority-name=ROLE_CUSTOMER
jwt.require-active=true
```

---

### 2.4. MS BRAND (Port 8083)

#### A. Entities

**Brand.java**
```java
@Entity
@Table(name = "Brand")
public class Brand {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BrandID")
    private Integer brandId;
    
    @Column(name = "BrandName", length = 100)
    private String brandName;
    
    @Column(name = "CountryOfOrigin", length = 100)
    private String countryOfOrigin;
}
```

**BlindBox.java** (Synchronized copy từ MSBlindBox)
```java
@Entity
@Table(name = "BlindBoxes")
public class BlindBox {
    // GIỐNG HỆT entity trong MSBlindBox
    // Đây là bản COPY để sync data
}
```

#### B. Repositories

**BrandRepository.java**
```java
@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
```

**BlindBoxRepository.java**
```java
@Repository
public interface BlindBoxRepository extends JpaRepository<BlindBox, Integer> {
}
```

#### C. Controllers

**BrandController.java** (Public API)
```java
@RestController
@RequestMapping("/api/brands")
public class BrandController {
    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Integer id)
}
```

**InternalBlindBoxController.java** (Internal API cho sync)
```java
@RestController
@RequestMapping("/api/internal/blindboxes")
public class InternalBlindBoxController {
    @PostMapping              // Sync CREATE từ MSBlindBox
    @GetMapping               // Get all
    @PutMapping("/{id}")      // Sync UPDATE từ MSBlindBox
    @DeleteMapping("/{id}")   // Sync DELETE từ MSBlindBox
}
```

#### D. Service Logic

**BrandServiceImpl.java**
- **getBrandById()**: Lấy brand info
- **addBlindBox()**: Nhận sync CREATE từ MSBlindBox
- **updateBlindBox()**: Nhận sync UPDATE từ MSBlindBox
- **deleteBlindBox()**: Nhận sync DELETE từ MSBlindBox
- **getAllBlindBoxes()**: Lấy all blind boxes (synced)

---

## 3. PHÂN TÍCH ĐIỂM THAY ĐỔI

### 3.1. Độ Phức Tạp Khi Thay Đổi (Từ CAO đến THẤP)

#### ⚠️ **CỰC KỲ KHÓ VÀ NHIỀU THAY ĐỔI NHẤT**

**1. Thay đổi Entity BlindBox (MSBlindBox & MSBrand)**
- **Lý do khó**: 
  - Entity tồn tại ở 2 services khác nhau
  - Có sync logic giữa 2 services
  - Có custom JPQL query với DTO projection
  - Có validation logic
  - Có nhiều dependency chains

**Các file cần thay đổi:**
```
MSBlindBox_SE184531/
├── entity/BlindBox.java                          ⚠️ ENTITY CHÍNH
├── dto/BlindBoxDTO.java                          ⚠️ DTO PROJECTION
├── repository/BlindBoxRepository.java            ⚠️ JPQL QUERY
├── service/BrandSyncClient.java                  ⚠️ SYNC CLIENT
├── service/impl/BlindBoxServiceImpl.java         ⚠️ VALIDATION + LOGIC
├── controller/BlindBoxController.java            → Request/Response
└── src/main/resources/application.properties     → DB config nếu đổi tên table

MSBrand_SE184531/
├── entity/BlindBox.java                          ⚠️ SYNC COPY ENTITY
├── repository/BlindBoxRepository.java            → Simple JPA
├── service/BrandService.java                     → Interface
├── service/impl/BrandServiceImpl.java            → Sync handlers
├── controller/InternalBlindBoxController.java    → Internal API
└── src/main/resources/application.properties     → DB config
```

**Ví dụ cụ thể**: Thêm field `description` vào BlindBox
1. Update `BlindBox.java` trong MSBlindBox
2. Update `BlindBox.java` trong MSBrand (phải giống hệt)
3. Update `BlindBoxDTO.java` nếu cần expose
4. Update JPQL query trong `BlindBoxRepository.findAllWithCategoryNameOrderByIdDesc()`
5. Update validation trong `BlindBoxServiceImpl.validateBlindBox()`
6. Database sẽ tự động update (ddl-auto=create)

---

#### 🔥 **RẤT KHÓ**

**2. Thay đổi API Gateway Routes**
- **Lý do khó**: 
  - Ảnh hưởng đến tất cả services
  - Phải sync với Swagger docs
  - Phải update JWT filter paths
  - Phải update SecurityConfig

**Các file cần thay đổi:**
```
APIGateway_SE184531/
├── src/main/resources/application.properties     ⚠️ ROUTES CONFIG
├── filter/JwtAuthenticationFilter.java           ⚠️ PATH BYPASS
├── config/SecurityConfig.java                    ⚠️ SECURITY RULES
└── pom.xml                                       → Nếu đổi tên service

MSAccount_SE184531/
├── src/main/resources/application.properties     ⚠️ SWAGGER URLS
└── Tất cả controller endpoints                   → Phải match routes

MSBlindBox_SE184531/
└── Tất cả controller endpoints                   → Phải match routes

MSBrand_SE184531/
└── Tất cả controller endpoints                   → Phải match routes
```

**Ví dụ cụ thể**: Đổi path từ `/api/blindboxes` → `/api/products`
1. Update Gateway routes predicates
2. Update JwtAuthenticationFilter bypass paths
3. Update SecurityConfig permit paths
4. Update MSBlindBox controller `@RequestMapping("/api/products")`
5. Update BrandSyncClient URLs
6. Update Swagger URLs trong MSAccount
7. Test tất cả endpoints

---

#### 🟡 **KHÁ KHÓ**

**3. Thay đổi JWT Claims hoặc Authentication Logic**
- **Lý do khó**: 
  - Ảnh hưởng cross-service
  - Phải sync secret key
  - Phải update filter và security config ở nhiều nơi

**Các file cần thay đổi:**
```
MSAccount_SE184531/
├── util/JwtUtil.java                             ⚠️ TOKEN GENERATION
├── service/impl/AuthServiceImpl.java             ⚠️ LOGIN LOGIC
├── dto/LoginResponse.java                        → Response structure
└── src/main/resources/application.properties     ⚠️ JWT CONFIG

APIGateway_SE184531/
├── filter/JwtAuthenticationFilter.java           ⚠️ TOKEN VALIDATION
└── src/main/resources/application.properties     ⚠️ SECRET KEY

MSBlindBox_SE184531/
├── config/SecurityConfig.java                    ⚠️ ROLE MAPPING
└── src/main/resources/application.properties     ⚠️ JWT CONFIG

MSBrand_SE184531/
├── config/SecurityConfig.java                    ⚠️ ROLE MAPPING
└── src/main/resources/application.properties     ⚠️ SECRET KEY
```

**Ví dụ cụ thể**: Thêm claim `department` vào JWT
1. Update `JwtUtil.generateToken()` để add claim
2. Update `LoginResponse` để include department
3. Update `AuthServiceImpl` để lấy department từ entity
4. Có thể cần update `SystemAccount` entity
5. Update Gateway filter nếu cần validate department
6. Update SecurityConfig nếu cần check department-based access

---

#### 🟢 **TRUNG BÌNH**

**4. Thêm/Sửa Entity mới (không liên quan sync)**
- **Ví dụ**: Thêm entity `Order`, `Review`

**Các file cần tạo/sửa:**
```
MS<Service>_SE184531/
├── entity/NewEntity.java                         📝 MỚI
├── repository/NewEntityRepository.java           📝 MỚI
├── dto/NewEntityDTO.java                         📝 MỚI (optional)
├── service/NewEntityService.java                 📝 MỚI
├── service/impl/NewEntityServiceImpl.java        📝 MỚI
├── controller/NewEntityController.java           📝 MỚI
└── APIGateway routes                             ⚠️ THÊM ROUTE
```

**Quy trình**:
1. Tạo Entity với JPA annotations
2. Tạo Repository extends JpaRepository
3. Tạo Service interface & implementation
4. Tạo Controller với endpoints
5. Add route trong Gateway
6. Update Gateway filter nếu cần authenticate
7. Test endpoints

---

#### ✅ **DỄ**

**5. Thêm Business Logic vào Service**
- **Ví dụ**: Thêm method validation, calculation

**Các file cần sửa:**
```
service/XxxService.java                           → Add interface method
service/impl/XxxServiceImpl.java                  → Implement logic
controller/XxxController.java                     → Expose endpoint (optional)
```

**6. Thêm Endpoint mới vào Controller hiện có**
- **Ví dụ**: Thêm `@GetMapping("/search")`

**Các file cần sửa:**
```
controller/XxxController.java                     → Add new endpoint
service/XxxService.java                           → Add service method
service/impl/XxxServiceImpl.java                  → Implement
```

**7. Thêm Custom Query vào Repository**
- **Ví dụ**: `findByNameContaining()`, `@Query`

**Các file cần sửa:**
```
repository/XxxRepository.java                     → Add query method
service/impl/XxxServiceImpl.java                  → Use new query
```

---

### 3.2. Ma Trận Thay Đổi Theo Loại

| Loại Thay Đổi | Entity | Repository | Service | Controller | DTO | Gateway | Config | Độ Khó |
|---------------|--------|------------|---------|------------|-----|---------|--------|--------|
| Thêm field vào Entity | ✅ | ⚠️ | ⚠️ | ❌ | ⚠️ | ❌ | ❌ | 🟢 |
| Đổi tên Entity/Table | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | 🔥 |
| Thêm Entity mới | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ❌ | 🟢 |
| Đổi endpoint path | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ | ⚠️ | 🟡 |
| Thêm validation | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Thay đổi JWT claims | ❌ | ❌ | ⚠️ | ❌ | ⚠️ | ✅ | ✅ | 🟡 |
| Thêm microservice mới | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 🔥 |
| Đổi port service | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| Thêm sync entity (như BlindBox) | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |

**Chú thích:**
- ✅ = Bắt buộc thay đổi
- ⚠️ = Có thể cần thay đổi
- ❌ = Không cần thay đổi

---

## 4. HƯỚNG DẪN THAY ĐỔI TỪNG LOẠI

### 4.1. THAY ĐỔI ENTITY CÓ SYNC (VÍ DỤ: BlindBox)

**Tình huống**: Thêm field `description: String` vào BlindBox

#### Bước 1: Update Entity trong MSBlindBox
**File**: `MSBlindBox_SE184531/entity/BlindBox.java`
```java
@Column(name = "Description", length = 500)
private String description;
```

#### Bước 2: Update Entity trong MSBrand (PHẢI GIỐNG HỆT)
**File**: `MSBrand_SE184531/entity/BlindBox.java`
```java
@Column(name = "Description", length = 500)
private String description;
```

#### Bước 3: Update DTO nếu cần expose
**File**: `MSBlindBox_SE184531/dto/BlindBoxDTO.java`
```java
private String description;

// Update constructor
public BlindBoxDTO(..., String description) {
    ...
    this.description = description;
}
```

#### Bước 4: Update JPQL Query
**File**: `MSBlindBox_SE184531/repository/BlindBoxRepository.java`
```java
@Query("SELECT new com.mss301.msblindbox_se184531.dto.BlindBoxDTO(" +
        "b.blindBoxId, b.name, b.categoryId, c.categoryName, b.brandId, " +
        "b.rarity, b.price, b.releaseDate, b.stock, b.description) " +  // ADD THIS
        "FROM BlindBox b " +
        "LEFT JOIN BlindBoxCategory c ON c.categoryId = b.categoryId " +
        "ORDER BY b.blindBoxId DESC")
List<BlindBoxDTO> findAllWithCategoryNameOrderByIdDesc();
```

#### Bước 5: Update Validation (nếu cần)
**File**: `MSBlindBox_SE184531/service/impl/BlindBoxServiceImpl.java`
```java
private void validateBlindBox(BlindBox blindBox) throws Exception {
    // Existing validations...
    
    // Add new validation
    if (blindBox.getDescription() == null || blindBox.getDescription().isEmpty()) {
        throw new Exception("Description is required");
    }
    if (blindBox.getDescription().length() > 500) {
        throw new Exception("Description must not exceed 500 characters");
    }
}
```

#### Bước 6: Test
```bash
# Restart services
# MSBlindBox sẽ tự động tạo column mới (ddl-auto=create)
# MSBrand sẽ tự động tạo column mới

# Test add new BlindBox với description
POST http://localhost:8080/api/blindboxes
{
    "name": "Test BlindBox Name More Than 10",
    "categoryId": 1,
    "brandId": 1,
    "rarity": "Rare",
    "price": 100.00,
    "stock": 50,
    "description": "This is a test description"
}

# Verify sync to MSBrand
GET http://localhost:8083/api/internal/blindboxes
```

---

### 4.2. ĐỔI TÊN SERVICE VÀ PACKAGE

**Tình huống**: Đổi từ `MSBlindBox` → `MSProduct`

#### Bước 1: Đổi tên thư mục
```
MSBlindBox_SE184531/ → MSProduct_SE184531/
```

#### Bước 2: Update pom.xml
**File**: `MSProduct_SE184531/pom.xml`
```xml
<artifactId>MSProduct_SE184531</artifactId>
<name>MSProduct_SE184531</name>
```

#### Bước 3: Đổi tên package
```
com.mss301.msblindbox_se184531 → com.mss301.msproduct_se184531
```
- Dùng IDE Refactor > Rename để đổi tất cả references
- Hoặc Find & Replace toàn bộ project

#### Bước 4: Đổi tên Application class
```java
MSBlindBoxSe184531Application → MSProductSe184531Application
```

#### Bước 5: Update application.properties
**File**: `MSProduct_SE184531/src/main/resources/application.properties`
```properties
spring.application.name=MSProduct_SE184531
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MSS301Summer25DBProduct;...
```

#### Bước 6: Update Gateway Routes
**File**: `APIGateway_SE184531/src/main/resources/application.properties`
```properties
# OLD
spring.cloud.gateway.routes[2].id=msblindbox-service
spring.cloud.gateway.routes[2].uri=http://localhost:8082
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/blindboxes/**,/api/categories/**

# NEW
spring.cloud.gateway.routes[2].id=msproduct-service
spring.cloud.gateway.routes[2].uri=http://localhost:8082
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/products/**,/api/categories/**

# Update Swagger docs route
spring.cloud.gateway.routes[6].id=msproduct-api-docs
spring.cloud.gateway.routes[6].uri=http://localhost:8082
spring.cloud.gateway.routes[6].predicates[0]=Path=/api-docs/msproduct,/api-docs/msproduct/**
spring.cloud.gateway.routes[6].filters[0]=RewritePath=/api-docs/msproduct(?<remaining>/?.*),/v3/api-docs$\{remaining}
```

#### Bước 7: Update Gateway Filter
**File**: `APIGateway_SE184531/filter/JwtAuthenticationFilter.java`
```java
// OLD
if (path.contains("/api/blindboxes") && request.getMethod().toString().equals("GET"))

// NEW
if (path.contains("/api/products") && request.getMethod().toString().equals("GET"))
```

#### Bước 8: Update Controllers
**File**: `MSProduct_SE184531/controller/ProductController.java`
```java
@RestController
@RequestMapping("/api/products")  // CHANGED
public class ProductController {
    // ...
}
```

#### Bước 9: Update Sync Client (nếu có)
**File**: `MSProduct_SE184531/service/BrandSyncClient.java`
- Update URLs nếu cần

**File**: `MSBrand_SE184531/controller/InternalProductController.java`
```java
@RequestMapping("/api/internal/products")  // CHANGED
```

#### Bước 10: Update Swagger URLs
**File**: `MSAccount_SE184531/src/main/resources/application.properties`
```properties
springdoc.swagger-ui.urls[1].name=MSProduct - Core Service
springdoc.swagger-ui.urls[1].url=http://localhost:8080/api-docs/msproduct
```

#### Bước 11: Test toàn bộ
```bash
# Restart tất cả services
# Test Gateway routing
# Test Swagger UI
# Test sync giữa services
```

---

### 4.3. THÊM ENDPOINT MỚI VÀO CONTROLLER HIỆN CÓ

**Tình huống**: Thêm search endpoint vào BlindBoxController

#### Bước 1: Thêm method vào Repository
**File**: `MSBlindBox_SE184531/repository/BlindBoxRepository.java`
```java
@Query("SELECT new com.mss301.msblindbox_se184531.dto.BlindBoxDTO(" +
        "b.blindBoxId, b.name, b.categoryId, c.categoryName, b.brandId, " +
        "b.rarity, b.price, b.releaseDate, b.stock) " +
        "FROM BlindBox b " +
        "LEFT JOIN BlindBoxCategory c ON c.categoryId = b.categoryId " +
        "WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
        "ORDER BY b.blindBoxId DESC")
List<BlindBoxDTO> searchByName(@Param("keyword") String keyword);
```

#### Bước 2: Thêm method vào Service Interface
**File**: `MSBlindBox_SE184531/service/BlindBoxService.java`
```java
public interface BlindBoxService {
    // Existing methods...
    List<BlindBoxDTO> searchBlindBoxes(String keyword);
}
```

#### Bước 3: Implement trong Service
**File**: `MSBlindBox_SE184531/service/impl/BlindBoxServiceImpl.java`
```java
@Override
public List<BlindBoxDTO> searchBlindBoxes(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return blindBoxRepository.findAllWithCategoryNameOrderByIdDesc();
    }
    return blindBoxRepository.searchByName(keyword.trim());
}
```

#### Bước 4: Thêm endpoint vào Controller
**File**: `MSBlindBox_SE184531/controller/BlindBoxController.java`
```java
@GetMapping("/search")
public ResponseEntity<?> searchBlindBoxes(@RequestParam String keyword) {
    try {
        List<BlindBoxDTO> results = blindBoxService.searchBlindBoxes(keyword);
        return ResponseEntity.ok(results);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

#### Bước 5: Update Gateway (KHÔNG CẦN nếu path đã được cover)
- `/api/blindboxes/search` đã được cover bởi `/api/blindboxes/**`

#### Bước 6: Update Filter (nếu cần)
- Nếu endpoint này là PUBLIC, đảm bảo Gateway filter bypass nó

#### Bước 7: Test
```bash
GET http://localhost:8080/api/blindboxes/search?keyword=test
```

---

### 4.4. THAY ĐỔI AUTHENTICATION LOGIC

**Tình huống**: Thêm kiểm tra email verification trước khi login

#### Bước 1: Thêm field vào Entity
**File**: `MSAccount_SE184531/entity/SystemAccount.java`
```java
@Column(name = "EmailVerified")
private Boolean emailVerified;
```

#### Bước 2: Update Service Logic
**File**: `MSAccount_SE184531/service/impl/AuthServiceImpl.java`
```java
@Override
public LoginResponse login(LoginRequest request) throws Exception {
    SystemAccount account = accountRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new Exception("Invalid email or password"));

    if (!account.getPassword().equals(request.getPassword())) {
        throw new Exception("Invalid email or password");
    }

    if (!account.getIsActive()) {
        throw new Exception("Account is not active");
    }

    // NEW VALIDATION
    if (account.getEmailVerified() == null || !account.getEmailVerified()) {
        throw new Exception("Email not verified. Please check your email.");
    }

    String token = jwtUtil.generateToken(
            account.getAccountId(),
            account.getEmail(),
            account.getRole(),
            account.getIsActive());

    return new LoginResponse(
            token,
            account.getAccountId(),
            account.getEmail(),
            account.getRole(),
            account.getIsActive());
}
```

#### Bước 3: Update DTO (nếu cần expose)
**File**: `MSAccount_SE184531/dto/LoginResponse.java`
```java
private Boolean emailVerified;  // Optional
```

#### Bước 4: Update JWT (nếu cần add claim)
**File**: `MSAccount_SE184531/util/JwtUtil.java`
```java
public String generateToken(Integer accountId, String email, Integer role, 
                           Boolean isActive, Boolean emailVerified) {
    return Jwts.builder()
            .subject(email)
            .claim("accountId", accountId)
            .claim(roleClaim, role)
            .claim(activeClaim, isActive)
            .claim("emailVerified", emailVerified)  // NEW
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
}
```

#### Bước 5: Test
```bash
POST http://localhost:8080/api/auth/login
{
    "email": "test@example.com",
    "password": "password"
}

# Should return error if email not verified
```

---

### 4.5. THÊM MICROSERVICE MỚI HOÀN TOÀN

**Tình huống**: Thêm MSOrder service (Port 8084)

#### Bước 1: Tạo Spring Boot project mới
```
MSOrder_SE184531/
├── src/
│   └── main/
│       ├── java/com/mss301/msorder_se184531/
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   └── OpenApiConfig.java
│       │   ├── controller/
│       │   │   └── OrderController.java
│       │   ├── entity/
│       │   │   └── Order.java
│       │   ├── repository/
│       │   │   └── OrderRepository.java
│       │   ├── service/
│       │   │   ├── OrderService.java
│       │   │   └── impl/OrderServiceImpl.java
│       │   └── MSOrderSe184531Application.java
│       └── resources/
│           └── application.properties
└── pom.xml
```

#### Bước 2: Setup application.properties
**File**: `MSOrder_SE184531/src/main/resources/application.properties`
```properties
server.port=8084
spring.application.name=MSOrder_SE184531

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MSS301Summer25DBOrder;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=12345
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true

jwt.secret=BlindBoxPE2025SecretKeyForJWTTokenGenerationMSS301Summer25Practice
jwt.role-claim=role
jwt.active-claim=isActive
jwt.admin-role-value=1
jwt.admin-authority-name=ROLE_ADMINISTRATOR
jwt.customer-authority-name=ROLE_CUSTOMER
jwt.require-active=true

springdoc.api-docs.enabled=true
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.enabled=false
```

#### Bước 3: Tạo Entity
**File**: `MSOrder_SE184531/entity/Order.java`
```java
@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;
    
    @Column(name = "AccountID")
    private Integer accountId;
    
    @Column(name = "OrderDate")
    private LocalDateTime orderDate;
    
    @Column(name = "TotalAmount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "Status", length = 50)
    private String status;
}
```

#### Bước 4: Tạo Repository, Service, Controller
```java
// Standard pattern như các service khác
```

#### Bước 5: Copy SecurityConfig từ service khác
**File**: `MSOrder_SE184531/config/SecurityConfig.java`
```java
// Copy từ MSBlindBox hoặc MSBrand
// Điều chỉnh role-based access nếu cần
```

#### Bước 6: Add routes vào Gateway
**File**: `APIGateway_SE184531/src/main/resources/application.properties`
```properties
# Add new route
spring.cloud.gateway.routes[9].id=msorder-service
spring.cloud.gateway.routes[9].uri=http://localhost:8084
spring.cloud.gateway.routes[9].predicates[0]=Path=/api/orders/**

# Add Swagger route
spring.cloud.gateway.routes[10].id=msorder-api-docs
spring.cloud.gateway.routes[10].uri=http://localhost:8084
spring.cloud.gateway.routes[10].predicates[0]=Path=/api-docs/msorder,/api-docs/msorder/**
spring.cloud.gateway.routes[10].filters[0]=RewritePath=/api-docs/msorder(?<remaining>/?.*),/v3/api-docs$\{remaining}
```

#### Bước 7: Update Gateway Filter (nếu cần custom logic)
**File**: `APIGateway_SE184531/filter/JwtAuthenticationFilter.java`
```java
// Thêm bypass logic nếu cần
if (path.contains("/api/orders/public")) {
    return chain.filter(exchange);
}
```

#### Bước 8: Update Swagger aggregation
**File**: `MSAccount_SE184531/src/main/resources/application.properties`
```properties
springdoc.swagger-ui.urls[3].name=MSOrder - Order Service
springdoc.swagger-ui.urls[3].url=http://localhost:8080/api-docs/msorder
```

#### Bước 9: Setup inter-service communication (nếu cần)
**Ví dụ**: MSOrder cần gọi MSBlindBox để check stock

**File**: `MSOrder_SE184531/config/AppConfig.java`
```java
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

**File**: `MSOrder_SE184531/service/BlindBoxClient.java`
```java
@Component
public class BlindBoxClient {
    private final RestTemplate restTemplate;
    
    @Value("${msblindbox.service.url}")
    private String msblindboxServiceUrl;
    
    public BlindBoxClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public BlindBoxDTO getBlindBox(Integer id) {
        return restTemplate.getForObject(
            msblindboxServiceUrl + "/api/blindboxes/" + id,
            BlindBoxDTO.class
        );
    }
}
```

**File**: `MSOrder_SE184531/src/main/resources/application.properties`
```properties
msblindbox.service.url=http://localhost:8082
```

#### Bước 10: Test toàn bộ
```bash
# Start MSOrder service
# Test direct access
GET http://localhost:8084/api/orders

# Test through Gateway
GET http://localhost:8080/api/orders

# Test Swagger
http://localhost:8080/swagger-ui.html

# Test inter-service call
```

---

### 4.6. ĐỔI ENDPOINT PATH TRONG SERVICE

**Tình huống**: Đổi `/api/blindboxes` → `/api/products`

#### Bước 1: Update Controller
**File**: `MSBlindBox_SE184531/controller/BlindBoxController.java`
```java
@RestController
@RequestMapping("/api/products")  // CHANGED
public class BlindBoxController {
    // All methods stay the same
}
```

#### Bước 2: Update Gateway Routes
**File**: `APIGateway_SE184531/src/main/resources/application.properties`
```properties
# OLD
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/blindboxes/**,/api/categories/**

# NEW
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/products/**,/api/categories/**
```

#### Bước 3: Update Gateway Filter
**File**: `APIGateway_SE184531/filter/JwtAuthenticationFilter.java`
```java
// OLD
if (path.contains("/api/blindboxes") && request.getMethod().toString().equals("GET"))

// NEW
if (path.contains("/api/products") && request.getMethod().toString().equals("GET"))
```

#### Bước 4: Update SecurityConfig
**File**: `APIGateway_SE184531/config/SecurityConfig.java`
```java
// OLD
.pathMatchers("/api/blindboxes/**").permitAll()

// NEW
.pathMatchers("/api/products/**").permitAll()
```

#### Bước 5: Update Sync Client (nếu có service khác call)
**File**: Bất kỳ service nào call BlindBox API
```java
// Update all URLs from /api/blindboxes to /api/products
```

#### Bước 6: Update Frontend/Client code
- Update tất cả API calls
- Update axios/fetch URLs

#### Bước 7: Test
```bash
# Test new path
GET http://localhost:8080/api/products
POST http://localhost:8080/api/products

# Verify old path returns 404
GET http://localhost:8080/api/blindboxes  # Should fail
```

---

## 5. CHECKLIST KHI THAY ĐỔI

### 5.1. THAY ĐỔI ENTITY

```
□ Update Entity class với @Column annotations
□ Update DTO (nếu có)
□ Update Repository queries (JPQL, @Query)
□ Update Service validation logic
□ Update Service business logic
□ Nếu là sync entity (như BlindBox):
  □ Update entity ở service thứ 2
  □ Update sync client
  □ Update internal controller
□ Test CRUD operations
□ Test validation
□ Test sync (nếu có)
□ Verify database schema (check columns)
```

### 5.2. ĐỔI TÊN SERVICE/PACKAGE

```
□ Rename folder/module
□ Update pom.xml (artifactId, name)
□ Refactor package name (IDE Refactor)
□ Rename Application class
□ Update application.properties (spring.application.name, datasource)
□ Update Gateway routes
□ Update Gateway filter paths
□ Update Gateway SecurityConfig
□ Update Swagger URLs trong MSAccount
□ Update inter-service client URLs
□ Update all import statements
□ Test all endpoints through Gateway
□ Test Swagger UI
□ Test inter-service communication
```

### 5.3. THAY ĐỔI ENDPOINT PATH

```
□ Update Controller @RequestMapping
□ Update Gateway routes predicates
□ Update Gateway filter bypass paths
□ Update Gateway SecurityConfig pathMatchers
□ Update client code calling this API
□ Update Swagger documentation
□ Test old path returns 404
□ Test new path works
□ Test authentication/authorization
```

### 5.4. THÊM MICROSERVICE MỚI

```
□ Create Spring Boot project
□ Setup pom.xml dependencies
□ Setup application.properties (port, datasource, jwt)
□ Create Entity, Repository, Service, Controller
□ Setup SecurityConfig
□ Setup OpenApiConfig
□ Add Gateway routes
□ Add Gateway Swagger docs route
□ Update MSAccount Swagger URLs
□ Setup RestTemplate (nếu cần inter-service call)
□ Create client classes (nếu cần)
□ Test direct service access
□ Test through Gateway
□ Test Swagger UI
□ Test inter-service communication
```

### 5.5. THAY ĐỔI JWT/AUTHENTICATION

```
□ Update JwtUtil (generation logic)
□ Update AuthService (login logic)
□ Update LoginResponse DTO
□ Update SystemAccount entity (nếu cần field mới)
□ Update Gateway filter (validation logic)
□ Update all services' application.properties (jwt.*)
□ Update SecurityConfig role mappings
□ Test login
□ Test token validation
□ Test role-based access
□ Test expired token handling
```

---

## 6. MẸO VÀ LƯU Ý QUAN TRỌNG

### 6.1. Gateway là điểm QUAN TRỌNG NHẤT
- **Mọi thay đổi endpoint/path PHẢI update Gateway**
- Gateway filter phải sync với SecurityConfig
- Routes phải match chính xác với Controller paths

### 6.2. Sync Entity (BlindBox) cần CẨN THẬN
- **2 entity PHẢI GIỐNG HỆT NHAU** (MSBlindBox & MSBrand)
- Xóa ở service sync TRƯỚC, sau đó mới xóa local
- Validate trước khi sync
- Handle sync errors gracefully

### 6.3. JWT Secret PHẢI GIỐNG NHAU
- Tất cả services phải dùng CÙNG secret key
- Gateway validate token với cùng secret
- Expiration time nên consistent

### 6.4. Database với ddl-auto=create
- **Mỗi lần restart sẽ DROP và CREATE lại tables**
- Phù hợp cho development/testing
- **Production NÊN DÙNG `validate` hoặc `none`**
- Có thể dùng `update` cho development

### 6.5. Port Numbers
- Gateway: 8080 (FIXED)
- MSAccount: 8081
- MSBlindBox: 8082
- MSBrand: 8083
- MSOrder (nếu thêm): 8084
- **Đổi port phải update Gateway routes và client URLs**

### 6.6. Swagger Aggregation
- MSAccount host Swagger UI (port 8081)
- Tất cả services expose `/v3/api-docs`
- Gateway route docs về từng service
- MSAccount aggregate tất cả docs

### 6.7. RestTemplate vs WebClient
- Hiện tại dùng RestTemplate (đơn giản, sync)
- Có thể upgrade lên WebClient (reactive, async)
- Cần AppConfig bean để inject

### 6.8. Validation Best Practices
- Validate trong Service layer (NOT Controller)
- Throw Exception với message rõ ràng
- Controller catch và return BadRequest
- Tách validation logic thành method riêng

### 6.9. Transaction Management
- Dùng `@Transactional(rollbackFor = Exception.class)`
- Đặc biệt quan trọng cho sync operations
- Nếu sync fail, rollback local changes

### 6.10. Testing Strategy
```bash
# 1. Test direct service
GET http://localhost:8082/api/blindboxes

# 2. Test through Gateway
GET http://localhost:8080/api/blindboxes

# 3. Test authentication
POST http://localhost:8080/api/auth/login

# 4. Test with JWT token
POST http://localhost:8080/api/blindboxes
Headers: Authorization: Bearer <token>

# 5. Test sync
POST → MSBlindBox → verify in MSBrand

# 6. Test Swagger
http://localhost:8080/swagger-ui.html
```

---

## 7. TROUBLESHOOTING COMMON ISSUES

### 7.1. Gateway 404 Not Found
**Nguyên nhân:**
- Route path không match controller path
- Service không chạy
- Port sai

**Giải pháp:**
```
1. Check service đang chạy: netstat -ano | findstr :8082
2. Check Gateway routes trong application.properties
3. Check Controller @RequestMapping path
4. Verify predicates pattern match
```

### 7.2. JWT Unauthorized
**Nguyên nhân:**
- Secret key khác nhau
- Token expired
- Claims không đúng
- Filter bypass path sai

**Giải pháp:**
```
1. Verify jwt.secret giống nhau ở tất cả services
2. Check token expiration time
3. Debug Gateway filter
4. Check bypass paths
```

### 7.3. Sync Failed giữa MSBlindBox và MSBrand
**Nguyên nhân:**
- MSBrand service down
- URL sai trong BrandSyncClient
- Entity structure khác nhau
- Network issue

**Giải pháp:**
```
1. Verify MSBrand đang chạy
2. Check msbrand.service.url trong properties
3. Verify 2 BlindBox entities GIỐNG HỆT NHAU
4. Test internal endpoint trực tiếp
5. Check logs cho details
```

### 7.4. Database Connection Failed
**Nguyên nhân:**
- SQL Server không chạy
- Database chưa tạo
- Credentials sai
- Port blocked

**Giải pháp:**
```
1. Start SQL Server
2. Database sẽ tự tạo nếu ddl-auto=create
3. Verify username/password
4. Check firewall/port 1433
```

### 7.5. Swagger UI Empty/Not Loading
**Nguyên nhân:**
- API docs URLs sai
- Gateway routes sai
- springdoc config sai

**Giải pháp:**
```
1. Check MSAccount application.properties swagger URLs
2. Verify Gateway routes cho /api-docs/*
3. Check springdoc.api-docs.enabled=true
4. Test direct access: http://localhost:8082/v3/api-docs
```

---

## 8. PATTERN MẪU

### 8.1. Standard Entity Pattern
```java
@Entity
@Table(name = "EntityName")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EntityID")
    private Integer entityId;
    
    @Column(name = "FieldName", nullable = false, length = 100)
    private String fieldName;
    
    // Add more fields
}
```

### 8.2. Standard Repository Pattern
```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, Integer> {
    Optional<Entity> findByFieldName(String fieldName);
    
    @Query("SELECT e FROM Entity e WHERE e.field = :value")
    List<Entity> customQuery(@Param("value") String value);
}
```

### 8.3. Standard Service Pattern
```java
public interface EntityService {
    List<Entity> getAll();
    Optional<Entity> getById(Integer id);
    Entity create(Entity entity) throws Exception;
    Entity update(Integer id, Entity entity) throws Exception;
    void delete(Integer id) throws Exception;
}

@Service
public class EntityServiceImpl implements EntityService {
    @Autowired
    private EntityRepository repository;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Entity create(Entity entity) throws Exception {
        validate(entity);
        return repository.save(entity);
    }
    
    private void validate(Entity entity) throws Exception {
        // Validation logic
    }
}
```

### 8.4. Standard Controller Pattern
```java
@RestController
@RequestMapping("/api/entities")
public class EntityController {
    @Autowired
    private EntityService service;
    
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(service.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> create(@RequestBody Entity entity) {
        try {
            return ResponseEntity.ok(service.create(entity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
```

### 8.5. Standard Sync Client Pattern
```java
@Component
public class ServiceSyncClient {
    private final RestTemplate restTemplate;
    private final String serviceUrl;
    
    public ServiceSyncClient(RestTemplate restTemplate,
                             @Value("${service.url}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }
    
    public void create(Entity entity) {
        restTemplate.postForObject(serviceUrl + "/api/internal/entities", 
                                   entity, String.class);
    }
    
    public void update(Integer id, Entity entity) {
        restTemplate.put(serviceUrl + "/api/internal/entities/" + id, entity);
    }
    
    public void delete(Integer id) {
        restTemplate.delete(serviceUrl + "/api/internal/entities/" + id);
    }
}
```

---

## 9. KẾT LUẬN

### Điểm khó nhất khi thay đổi (theo thứ tự):

1. **⚠️ Thay đổi Sync Entity (BlindBox)**: 
   - 2 services phải sync
   - Nhiều dependency chains
   - Có JPQL query phức tạp

2. **🔥 Thay đổi Gateway Routes**: 
   - Ảnh hưởng tất cả services
   - Phải sync nhiều config files
   - Dễ miss endpoints

3. **🟡 Đổi tên Service/Package**: 
   - Refactor toàn bộ
   - Update nhiều references
   - Dễ miss import statements

4. **🟢 Thêm Entity/Endpoint mới**: 
   - Follow patterns
   - Ít dependencies
   - Straightforward

### Nguyên tắc vàng:

✅ **Luôn update Gateway khi thay đổi endpoints**
✅ **Sync entities phải giống hệt nhau**
✅ **JWT secret phải consistent**
✅ **Test từng layer: Direct → Gateway → Auth**
✅ **Follow existing patterns**
✅ **Validate trước khi save**
✅ **Handle exceptions gracefully**
✅ **Document changes trong code comments**

### Workflow chuẩn khi thay đổi:

1. **Plan**: Xác định scope thay đổi
2. **Entity**: Update entities và DTOs
3. **Repository**: Update queries
4. **Service**: Update logic và validation
5. **Controller**: Update endpoints
6. **Gateway**: Update routes và filters
7. **Config**: Update properties files
8. **Test**: Test từng layer
9. **Document**: Update docs

---

**LƯU Ý CUỐI**: Tài liệu này được tạo dựa trên cấu trúc hiện tại. Khi cấu trúc thay đổi, cần update lại tài liệu này.

**Version**: 1.0
**Last Updated**: November 9, 2025
**Author**: System Analysis
