package main;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Lists;
import com.pi.gae.community.parade.service.CommunityParadeRestService;
import com.pi.gae.community.parade.util.FirebaseManager;

public class App {
  static final Log log = LogFactory.getLog(App.class);

  private static JacksonJsonProvider jsonProvider(ObjectMapper objectMapper) {
    JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
    provider.setMapper(objectMapper);
    return provider;
  }

  public static void main(String[] args) {
    FirebaseManager.createInstance("https://community-parade-1.firebaseio.com");

    JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();

    List<Object> apis = new ArrayList<>();
    apis.add(new CommunityParadeRestService());

    // Service Bean : Inject Rest Apis or Controllers
    serverFactory.setServiceBeans(apis);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    serverFactory.setProviders(Lists.newArrayList(jsonProvider(objectMapper)));

    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    log.info("Port value: " + port);
    serverFactory.setAddress(String.format("http://localhost:%d", port));
    BindingFactoryManager manager =
        serverFactory.getBus().getExtension(BindingFactoryManager.class);
    JAXRSBindingFactory bindingfactory = new JAXRSBindingFactory();
    bindingfactory.setBus(serverFactory.getBus());
    manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, bindingfactory);

    serverFactory.create();

    log.info("Application finished loading resources. Ready to serve");
  }
}
