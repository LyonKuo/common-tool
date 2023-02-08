package com.common.cyberark.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GetPasswordRequest {

    private String appId = null;

    private String safe = null;

    private String folder = null;

    private String object = null;

    private String reason = null;

    private String sign = null;
}
