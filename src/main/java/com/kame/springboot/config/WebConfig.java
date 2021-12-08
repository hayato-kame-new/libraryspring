package com.kame.springboot.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Spring4→Spring5になったから WebMvcConfigurerAdapterが非推奨になってしまった
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
// 代わりに WebMvcConfigurerインタフェースを実装すればいい

// public class WebConfig extends WebMvcConfigurerAdapter {

//全てのコントローラーに対して共通処理を行う
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        // 1ページに表示する最大件数(10件)を設定する
        // resolver.setMaxPageSize(10);
        
       // こっちを使う
        resolver.setFallbackPageable(PageRequest.of(0, 10));
             
        argumentResolvers.add(resolver);
    }
}