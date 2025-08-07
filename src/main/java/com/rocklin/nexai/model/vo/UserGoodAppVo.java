package com.rocklin.nexai.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName UserGoodAppVo
 * @Description 精选应用用户VO
 * @Author: rocklin
 * @Date 2025/8/7 17:15
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGoodAppVo implements Serializable {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;
}
