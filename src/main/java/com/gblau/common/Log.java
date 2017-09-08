package com.gblau.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封装的日志类，支持从当前线程动态获取Class创建Log并输出日志，可以适当简化某些地方的代码量。
 * 动态获取Class可能会降低效率，谨慎使用。
 * @author gblau
 * @date 2017-03-18
 */
public abstract class Log {
    /**
     * 通过class获得Logger
     * @param clazz
     * @return
     */
    public static Logger get(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 获得Logger
     * @param name 自定义的日志发出者名称
     * @return Logger
     */
    public static Logger get(String name) {
        return LoggerFactory.getLogger(name);
    }

    //------------------------ Trace

    /**
     * Trace等级日志，小于debug
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void trace(String format, Object... arguments) {
        trace(innerGet(), format, arguments);
    }

    /**
     * Trace等级日志，小于Debug
     * @param log 日志对象
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void trace(Logger log, String format, Object... arguments) {
        log.trace(format, arguments);
    }

    //------------------------ debug

    /**
     * Debug等级日志，小于Info
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void debug(String format, Object... arguments) {
        debug(innerGet(), format, arguments);
    }

    /**
     * Debug等级日志，小于Info
     * @param log 日志对象
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void debug(Logger log, String format, Object... arguments) {
        log.debug(format, arguments);
    }

    //------------------------ info

    /**
     * Info等级日志，小于Warn
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void info(String format, Object... arguments) {
        info(innerGet(), format, arguments);
    }

    /**
     * Info等级日志，小于Warn
     * @param log 日志对象
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void info(Logger log, String format, Object... arguments) {
        log.info(format, arguments);
    }

    //------------------------ warn

    /**
     * Warn等级日志，小于Error
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void warn(String format, Object... arguments) {
        warn(innerGet(), format, arguments);
    }

    /**
     * Warn等级日志，小于Error
     * @param log 日志对象
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void warn(Logger log, String format, Object... arguments) {
        log.warn(format, arguments);
    }

    /**
     * Warn等级日志，小于Error
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param e 需在日志中堆栈打印的异常
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void warn(Throwable e, String format, Object... arguments) {
        warn(innerGet(), format(format, arguments), e);
    }

    /**
     * Warn等级日志，小于Error
     * @param log 日志对象
     * @param e 需在日志中堆栈打印的异常
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void warn(Logger log, String format, Throwable e, Object... arguments) {
        log.warn(format(format, arguments), e);
    }

    //------------------------ error
    
    /**
     * Error等级日志
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void error(String format, Object... arguments) {
        error(innerGet(), format, arguments);
    }

    /**
     * Error等级日志
     * @param log 日志对象
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void error(Logger log, String format, Object... arguments) {
        log.error(format, arguments);
    }

    /**
     * Error等级日志
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param e 需在日志中堆栈打印的异常
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void error(String format, Throwable e, Object... arguments) {
        error(innerGet(), format(format, arguments), e);
    }

    /**
     * Error等级日志
     * 由于动态获取Logger，效率较低，建议在非频繁调用的情况下使用！！
     * @param log 日志对象
     * @param e 需在日志中堆栈打印的异常
     * @param format 格式文本，{} 代表变量
     * @param arguments 变量对应的参数
     */
    public static void error(Logger log, String format, Throwable e, Object... arguments) {
        log.error(format(format, arguments), e);
    }

    /**
     * 格式化文本
     * @param message 原文本，参数用{}表示
     * @param arguments
     * @return
     */
    private static String format(String message, Object... arguments) {
        return String.format(message.replace("{}", "%s"), arguments);
    }

    /**
     * 动态获取一个Logger
     * @return
     */
    private static Logger innerGet() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        return LoggerFactory.getLogger(stackTraces[3].getClassName());
    }
}
