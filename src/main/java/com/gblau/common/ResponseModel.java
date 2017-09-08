package com.gblau.common;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * 利用Http状态码作为返回码，可以自定义信息返回
 * @see HttpStatus
 * @author gblau
 * @date 2016-11-12
 */
public class ResponseModel<T> {
    private HttpStatus status;
    private T data;
    private String message;

    private ResponseModel(HttpStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ResponseModel setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseModel setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseModel setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 200：操作成功。
     * @return
     */
    public static ModelBuilder ok() {
        return status(HttpStatus.OK);
    }

    /**
     * 操作成功，并且返回数据。
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseModel<T> ok(T data) {
        return ok().body(data);
    }

    /**
     * 202：登录成功。
     * @return
     */
    public static ModelBuilder accepted() {
        return status(HttpStatus.ACCEPTED);
    }

    /**
     * 406：拒绝操作。例如登录失败、没有权限访问
     * @return
     */
    public static ModelBuilder rejected() {
        return status(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * 400：错误的请求
     * @return
     */
    public static ModelBuilder badRequest() {
        return status(HttpStatus.BAD_REQUEST);
    }

    /**
     * 403：没有权限进行相关操作。
     * @return
     */
    public static ModelBuilder forbidden() {
        return status(HttpStatus.FORBIDDEN);
    }

    /**
     * 500：服务器内部错误
     * @return
     */
    public static ModelBuilder internerServerError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 通过设置状态码获取一个ModelBuilder。可以设置自定义状态码
     * @param status
     * @return
     */
    public static ModelBuilder status(HttpStatus status) {
        Assert.notNull(status, "HttpStatus不能为null");
        return new DefaultBuilder(status);
    }

    /**
     * ResponseModel的构造器接口。目的是构造一个ResponseModel
     */
    public interface ModelBuilder {
        /**
         * 构建一个含有message的ResponseModel
         * @param message
         * @return
         */
        ResponseModel message(String message);

        /**
         * 构建一个ResponseModel
         * @return
         */
        ResponseModel build();

        /**
         * 构建一个带返回对象的ResponseModel
         * @param data
         * @param <T>
         * @return
         */
        <T> ResponseModel<T> body(T data);

        /**
         * 构建一个带返回对象并且含有message的ResponseModel
         * @param data
         * @param message
         * @param <T>
         * @return
         */
        <T> ResponseModel<T> messageWithBody(T data, String message);
    }

    private static class DefaultBuilder implements ModelBuilder {
        private HttpStatus status;

        private DefaultBuilder(HttpStatus status) {
            this.status = status;
        }

        @Override
        public ResponseModel message(String message) {
            return new ResponseModel(status, null, message);
        }

        @Override
        public ResponseModel build() {
            return body(null);
        }

        @Override
        public <T> ResponseModel<T> body(T data) {
            return new ResponseModel<>(status, data, null);
        }

        @Override
        public <T> ResponseModel<T> messageWithBody(T data, String message) {
            return new ResponseModel<>(status, data, message);
        }
    }
}
