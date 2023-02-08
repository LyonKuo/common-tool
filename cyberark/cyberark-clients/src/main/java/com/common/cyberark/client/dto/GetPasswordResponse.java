package com.common.cyberark.client.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetPasswordResponse {

    private String code = null;

    private String msg = null;

    private String password = null;
}
