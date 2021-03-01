package com.sailing.hessian;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: packetresend
 * @description:
 * @author: wangsw
 * @create: 2020-11-30 15:47
 */
@Data
public class RespData<T> implements Serializable {

    private int code;//返回码，见NodeStateEnum
    private String reason;//返回说明
    private T data;//返回内容，json数据

    public boolean isSuccess(){
        return  code==200;
    }
}
