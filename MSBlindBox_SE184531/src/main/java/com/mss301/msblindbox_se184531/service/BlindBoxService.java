package com.mss301.msblindbox_se184531.service;

import com.mss301.msblindbox_se184531.dto.BlindBoxDTO;
import com.mss301.msblindbox_se184531.entity.BlindBox;

import java.util.List;

public interface BlindBoxService {
    List<BlindBoxDTO> getAllBlindBoxes();

    BlindBox addBlindBox(BlindBox blindBox) throws Exception;

    BlindBox updateBlindBox(Integer id, BlindBox blindBox) throws Exception;

    void deleteBlindBox(Integer id) throws Exception;
}
