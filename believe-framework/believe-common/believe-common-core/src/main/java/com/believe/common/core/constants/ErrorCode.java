package com.believe.common.core.constants;

public interface ErrorCode {

    // 1xxx - 参数错误
    int PARAM_MISSING = 1001;
    int PARAM_INVALID = 1002;

    // 2xxx - 业务错误
    int BIZ_GENERAL = 2000;
    int BIZ_DATA_NOT_FOUND = 2001;

    // 3xxx - 系统错误
    int SYS_GENERAL = 3000;
    int SYS_TIMEOUT = 3001;

    // 4xxx - 权限错误
    int AUTH_UNAUTHORIZED = 4001;
    int AUTH_FORBIDDEN = 4003;

    // 5xxx - 服务调用错误
    int RPC_GENERAL = 5000;
    int RPC_TIMEOUT = 5001;
}
