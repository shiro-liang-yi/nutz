package net.wendal.nutzbook.service;

/**
 * 
 * @author liangshuai
 * @date 2018年6月11日 下午4:53:47
 */
public interface EmailService {

    boolean send(String to, String subject, String html);

}