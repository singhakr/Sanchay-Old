package sanchay.servers.clients;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import jmathlib.toolbox.jmathlib.system.java;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import sanchay.server.dto.auth.model.domain.SanchayUserDTO;

public class SanchaySpringClientUtils {
    
    public static SanchaySpringRestClient getSanchaySpringRestClientInstance() throws IOException
    {
        SanchaySpringRestClient sanchaySpringRestClient = new SanchaySpringRestClient();
        
        RestTemplate restTemplate = getRestTemplateInstance(sanchaySpringRestClient);
        
        sanchaySpringRestClient.setRestTemplate(restTemplate);

        sanchaySpringRestClient.init();
        
        return sanchaySpringRestClient;
    }

    public static RestTemplate getRestTemplateInstance(SanchaySpringRestClient sanchaySpringRestClient) {
        
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        
        List<ClientHttpRequestInterceptor> interceptors
                = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        
        interceptors.add(new RestTemplateHeaderModifierInterceptor(sanchaySpringRestClient));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    public static ModelMapper getModelMapperInstance() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    public static ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 500000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(timeout);
        
//        ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(
//                new SimpleClientHttpRequestFactory()
//        );
        
        return clientHttpRequestFactory;
    }
    
    public static Properties loadPropertiesFile(String propertiesPath) {
        Properties properties = null;
        try (InputStream input = new FileInputStream(propertiesPath)) {

            properties = new Properties();

            // load a properties file
            properties.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }

    public static String buildClientAnnotationFolderPath(SanchayUserDTO user)
    {
        String path = SanchaySpringRestClient.SANCHAY_CONFIG_PROPERTIES.getProperty("CLIENT_ANNOTATION_FOLDER");

//        String organisationName = user.getCurrentOrganisation().getName();
//
//        String languageName = user.getCurrentLanguage().getName();
//
//        String annotationLevel = user.getCurrentAnnotationLevel().getName();
        String organisationName = user.getCurrentOrganisationName();

        String languageName = user.getCurrentLanguageName();

        String annotationLevel = user.getCurrentAnnotationLevelName();

        String username = user.getUsername();

        path = path + "/annotation/" + organisationName + "/" + languageName
                + "/" + annotationLevel + "/" + username;

        path = Paths.get(path).normalize().toString();

        return path;
    }

    public static Object getFirstMapKey(Map map)
    {
        Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) map.entrySet().iterator().next();
        String key = (String) entry.getKey();
//        String value = entry.getValue();

        return key;
    }

    public static String getAbsolutePathOnClient(SanchayUserDTO user, String relativePathOnServer)
    {
        String defaultDirPathOnClient = buildClientAnnotationFolderPath(user);

        File clientFile = new File(defaultDirPathOnClient, relativePathOnServer);

        String absolutePathOnClient = clientFile.getAbsolutePath();

        absolutePathOnClient = String.valueOf(Paths.get(absolutePathOnClient).normalize());

        return absolutePathOnClient;
    }

    public static FileSystemResource getFileResource(File file)
    {
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        return fileSystemResource;
    }

    public static String readFileAsString(String filePath)throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(filePath)));
        return data;
    }
}
