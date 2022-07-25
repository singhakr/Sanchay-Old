/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.clients;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 *
 * @author User
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SanchaySpringClientProperties.class)
public class SanchaySpringRestClientConfig {
    
    SanchaySpringRestClient sanchaySpringRestClient;

//    @Bean
//    public RestTemplate restTemplate() {
//        
//        
//        
//        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
//        
//        List<ClientHttpRequestInterceptor> interceptors
//                = restTemplate.getInterceptors();
//        if (CollectionUtils.isEmpty(interceptors)) {
//            interceptors = new ArrayList<>();
//        }
//        interceptors.add(new RestTemplateHeaderModifierInterceptor());
//        restTemplate.setInterceptors(interceptors);
//        return restTemplate;
//    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 500000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(timeout);
        
//        ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(
//                new SimpleClientHttpRequestFactory()
//        );
        
        return clientHttpRequestFactory;
    }
    
//    @Bean
//    public ModelMapper modelMapper() {
//            ModelMapper modelMapper = new ModelMapper();
//            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//            return modelMapper;
//    }
    
    public static void main(String args[])
    {
//        RestClientConfig restClientConfig = new RestClientConfig();
//
//        restClientConfig.
    }
}
