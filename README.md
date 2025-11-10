# MSS301 Microservices – Hướng dẫn nhanh & thay đổi (TIẾNG VIỆT)

Mục tiêu: dùng như “bài mẫu” lúc đi thi. Ưu tiên đổi qua cấu hình (properties); chỉ sửa code khi thật cần.

## 1) Services, Ports, DB
- MSAccount_SE184531 (8081) → DB: MSS301Summer25DBAccount
- MSBlindBox_SE184531 (8082) → DB: MSS301Summer25DBBlindBox
- MSBrand_SE184531 (8083) → DB: MSS301Summer25DBBrand
- APIGateway_SE184531 (8080)

Start order: 8081 → 8082 → 8083 → 8080.

## 2) Swagger/OpenAPI
- UI tổng hợp: MSAccount (8081). Gateway proxy API-docs của các service.
- Test UI:
  - http://localhost:8081/swagger-ui.html (chọn definition trỏ qua Gateway)
- Gateway API-docs (JSON):
  - http://localhost:8080/api-docs/msaccount
  - http://localhost:8080/api-docs/msblindbox
  - http://localhost:8080/api-docs/msbrand

Bật/tắt (khuyến nghị bật trong thi):
- application.properties
  - springdoc.api-docs.enabled=true
  - springdoc.swagger-ui.enabled=true (UI chủ yếu ở 8081)

## 3) Gateway routes (điểm sửa duy nhất)
File: `APIGateway_SE184531/src/main/resources/application.properties`
- Tuyến nghiệp vụ:
  - `/api/auth/**` → http://localhost:8081
  - `/api/brands/**` → http://localhost:8083
  - `/api/blindboxes/**,/api/categories/**` → http://localhost:8082
  - `/api/internal/blindboxes/**` → http://localhost:8083
- API-docs rewrite (1 regex, không chèn khoảng trắng):
  - `/api-docs/msaccount(?<remaining>/.*)` → `/v3/api-docs${remaining}` → uri 8081
  - `/api-docs/msblindbox(?<remaining>/.*)` → `/v3/api-docs${remaining}` → uri 8082
  - `/api-docs/msbrand(?<remaining>/.*)` → `/v3/api-docs${remaining}` → uri 8083

Đổi host/port service → chỉ sửa `uri` tương ứng.

### 3.1 Khi ĐỔI TÊN SERVICE hoặc ĐỔI PREFIX API
Giả sử đổi:
- MSAccount → NewAccountService (chạy 8091), API từ `/api/auth/**` → `/api/new-auth/**`
- MSBlindBox → NewBoxService (8092), API từ `/api/blindboxes/**` → `/api/boxes/**`; `/api/categories/**` giữ nguyên
- MSBrand → NewBrandService (8093), API từ `/api/brands/**` → `/api/new-brands/**`; internal từ `/api/internal/blindboxes/**` → `/api/internal/boxes/**`

Sửa properties Gateway như sau (KHÔNG đụng code):
- Tuyến nghiệp vụ:
  - `spring.cloud.gateway.routes[0].predicates[0]=Path=/api/new-auth/**`
  - `spring.cloud.gateway.routes[0].uri=http://localhost:8091`
  - `spring.cloud.gateway.routes[1].predicates[0]=Path=/api/new-brands/**`
  - `spring.cloud.gateway.routes[1].uri=http://localhost:8093`
  - `spring.cloud.gateway.routes[2].predicates[0]=Path=/api/boxes/**,/api/categories/**`
  - `spring.cloud.gateway.routes[2].uri=http://localhost:8092`
  - `spring.cloud.gateway.routes[3].predicates[0]=Path=/api/internal/boxes/**`
  - `spring.cloud.gateway.routes[3].uri=http://localhost:8093`

- API-docs (đặt nhãn mới cho dễ nhớ, URI trỏ service mới):
  - Account docs:
    - `spring.cloud.gateway.routes[5].id=newaccount-api-docs`
    - `spring.cloud.gateway.routes[5].uri=http://localhost:8091`
    - `spring.cloud.gateway.routes[5].predicates[0]=Path=/api-docs/newaccount,/api-docs/newaccount/**`
    - `spring.cloud.gateway.routes[5].filters[0]=RewritePath=/api-docs/newaccount(?<remaining>/.*/),/v3/api-docs${remaining}`
  - BlindBox/Boxes docs (→8092): tương tự với tiền tố `/api-docs/newbox`
  - Brand docs (→8093): tương tự với tiền tố `/api-docs/newbrand`

Ghi nhớ:
- Phía trước (Path=...) là đường dẫn client sẽ gọi; phía sau (uri=...) là host:port thật của service.
- Khối rewrite API-docs KHÔNG phụ thuộc tên app; bạn chỉ cần đổi tiền tố sau `/api-docs/` và `uri` đúng service.

### 3.2 Giải thích từng dòng cấu hình API-docs (ví dụ cho MSBrand)
Trong `APIGateway_SE184531/src/main/resources/application.properties`:
```properties
spring.cloud.gateway.routes[7].id=msbrand-api-docs
spring.cloud.gateway.routes[7].uri=http://localhost:8083
spring.cloud.gateway.routes[7].predicates[0]=Path=/api-docs/msbrand,/api-docs/msbrand/**
spring.cloud.gateway.routes[7].filters[0]=RewritePath=/api-docs/msbrand(?<remaining>/?.*),/v3/api-docs$\{remaining}
```
- `id`: tên route để đọc log/debug (đổi tự do).
- `uri`: host:port service MSBrand.
- `Path`: URL client gọi (prefix `msbrand` do bạn tự đặt, có thể đổi thành `newbrand`).
- `RewritePath`: luôn rewrite sang `/v3/api-docs${remaining}` vì springdoc cố định ở service.
  - `(?<remaining>/?.*)` bắt phần đuôi (có/không có `/`).
  - Trong .properties, viết `$\{remaining}` để tránh Spring coi là placeholder.

### 3.3 Ví dụ tương tự cho MSAccount và MSBlindBox
MSAccount (uri 8081):
```properties
spring.cloud.gateway.routes[5].id=msaccount-api-docs
spring.cloud.gateway.routes[5].uri=http://localhost:8081
spring.cloud.gateway.routes[5].predicates[0]=Path=/api-docs/msaccount,/api-docs/msaccount/**
spring.cloud.gateway.routes[5].filters[0]=RewritePath=/api-docs/msaccount(?<remaining>/?.*),/v3/api-docs$\{remaining}
```
- Đổi nhãn: thay `msaccount` → `newaccount` ở cả Path và RewritePath.
- Đổi port: chỉ chỉnh `uri=http://localhost:8091`.

MSBlindBox (uri 8082):
```properties
spring.cloud.gateway.routes[6].id=msblindbox-api-docs
spring.cloud.gateway.routes[6].uri=http://localhost:8082
spring.cloud.gateway.routes[6].predicates[0]=Path=/api-docs/msblindbox,/api-docs/msblindbox/**
spring.cloud.gateway.routes[6].filters[0]=RewritePath=/api-docs/msblindbox(?<remaining>/?.*),/v3/api-docs$\{remaining}
```
- Đổi nhãn: thay `msblindbox` → `newbox` ở Path/RewritePath.
- Đổi port: chỉnh `uri=http://localhost:8092`.

### 3.4 Checklist đổi tên nhanh (prefix + port)
1) Chọn prefix mới cho từng service: `newaccount`, `newbox`, `newbrand`…
2) Sửa 3 khối API-docs trong Gateway:
   - Đổi tiền tố sau `/api-docs/` ở `Path=` và trong `RewritePath=`.
   - Cập nhật `uri=` đúng host:port service.
3) Nếu API nghiệp vụ đổi prefix (vd `/api/auth/**` → `/api/new-auth/**`), sửa các tuyến business tương ứng.
4) Restart Gateway (8080). Kiểm tra:
   - Trực tiếp service: `http://<host>:<port>/v3/api-docs` trả JSON.
   - Qua Gateway: `http://localhost:8080/api-docs/<prefix_moi>` trả JSON tương ứng.

### 3.5 Ví dụ thực tế – đổi MSBrand → NewBrandService (port 8093)
Mục tiêu: Client sẽ gọi `http://localhost:8080/api-docs/newbrand` để xem OpenAPI của service mới ở cổng 8093.

1) Sửa Gateway properties (thay 2 dòng Path/RewritePath + 1 dòng uri):
```properties
spring.cloud.gateway.routes[7].id=newbrand-api-docs
spring.cloud.gateway.routes[7].uri=http://localhost:8093
spring.cloud.gateway.routes[7].predicates[0]=Path=/api-docs/newbrand,/api-docs/newbrand/**
spring.cloud.gateway.routes[7].filters[0]=RewritePath=/api-docs/newbrand(?<remaining>/?.*),/v3/api-docs$\{remaining}
```

2) Khởi động NewBrandService ở port 8093 (đảm bảo truy cập trực tiếp OK):
- Mở `http://localhost:8093/v3/api-docs` → thấy JSON.

3) Test qua Gateway:
- Mở `http://localhost:8080/api-docs/newbrand` → phải thấy JSON giống bước 2.

4) Nếu đổi luôn API nghiệp vụ (ví dụ `/api/brands/**` → `/api/new-brands/**`):
```properties
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/new-brands/**
spring.cloud.gateway.routes[1].uri=http://localhost:8093
```
Sau đó gọi thử: `curl http://localhost:8080/api/new-brands` (hoặc endpoint phù hợp) để xác nhận.

## 4) JWT – đổi role/claim không sửa code
Đổi tại: application.properties của MSAccount + MSBlindBox.

Common keys (present already):
- jwt.role-claim=role
- jwt.active-claim=isActive
- jwt.admin-role-value=1
- jwt.admin-authority-name=ROLE_ADMINISTRATOR
- jwt.customer-authority-name=ROLE_CUSTOMER
- jwt.require-active=true|false (MSBlindBox; set false if active is checked at login only)

Hành vi
- MSAccount `JwtUtil.generateToken` phát claim theo tên đã cấu hình
- MSBlindBox `SecurityConfig.JwtAuthenticationFilter` đọc claim và map quyền theo `jwt.admin-role-value`
- Đổi admin role sang 4: đặt `jwt.admin-role-value=4` (không cần sửa code)
- Đổi tên claim role: `jwt.role-claim=yourRoleClaim` trên cả MSAccount & MSBlindBox
- Tắt check active ở service: `jwt.require-active=false`

## 5) Điểm chạm để đổi logic nhanh
- MSBlindBox
  - Validate rules: `service/impl/BlindBoxServiceImpl.java#validateBlindBox`
  - List shape/order: `repository/BlindBoxRepository.java` (JPQL `findAllWithCategoryNameOrderByIdDesc`)
  - Sync MSBrand endpoints: `service/BrandSyncClient.java` (edit paths/URIs only here)
  - Transaction flow: `service/impl/BlindBoxServiceImpl.java` (Add/Update: local→remote; Delete: remote→local)
- MSBrand
  - Internal sync API: `controller/InternalBlindBoxController.java`
  - Nghiệp vụ: `service/impl/BrandServiceImpl.java`
- MSAccount
  - Login rules (email/password/isActive): `service/impl/AuthServiceImpl.java`
  - Token fields: `util/JwtUtil.java#generateToken`

## 6) Kịch bản đổi đề “trong 1 phút”
- Change admin role from 1 → 4:
  - MSBlindBox: `jwt.admin-role-value=4`
- Rename role claim to `userRole`:
  - MSAccount + MSBlindBox: `jwt.role-claim=userRole`
- Tắt kiểm tra active ở service:
  - MSBlindBox: `jwt.require-active=false`
- Change MSBrand internal route path:
  - MSBrand: update `InternalBlindBoxController` mappings
  - MSBlindBox: update `BrandSyncClient`
  - Gateway: ensure `/api/internal/blindboxes/**` points to the new path if changed
- Change list sorting:
  - Update ORDER BY in `BlindBoxRepository` JPQL

## 7) Script test nhanh (curl)
- Login (qua Gateway):
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"yourPassword"}'
```
- List (public):
```bash
curl http://localhost:8080/api/blindboxes
```
- Create/Update/Delete (admin):
```bash
curl -X POST http://localhost:8080/api/blindboxes \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"name":"Sample BlindBox 123456789","categoryId":1,"brandId":1,"rarity":"SSR","price":99.9,"stock":10}'
```

## 8) Clean code & loại bỏ dư thừa
- Đã gỡ annotation Swagger thừa ở controller nội bộ MSBrand.
- `config/AppConfig` (RestTemplate) ĐANG được dùng bởi `BrandSyncClient`.
- Không thấy class/hàm thừa. Nếu xoá 1 tính năng, xoá cả controller/service/repository tương ứng.

## 9) FAQ – lỗi thường gặp
- 404 `/api-docs/*`: sai chính tả key `spring.cloud.gateway.routes[...]` hoặc service đích chưa chạy
- 403 `/api-docs/*`: service đích chưa whitelist `/v3/api-docs` trong SecurityConfig
- Token bị từ chối dù hợp lệ: lệch tên/giá trị claim role → đồng bộ properties giữa MSAccount & MSBlindBox

## 10) Checklist trước khi nộp
- 4 services + gateway chạy OK
- Swagger UI mở tại 8081; API-docs của mọi service xem qua 8080
- Login lấy token được; CRUD BlindBox chạy, sync MSBrand hoạt động
- Properties khớp đề: DB name, port, claim/role, route Gateway
