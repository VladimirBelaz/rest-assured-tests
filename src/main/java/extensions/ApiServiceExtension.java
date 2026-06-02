// extensions/ApiServiceExtension.java
package extensions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import services.PetApi;

import java.lang.reflect.Parameter;

public class ApiServiceExtension implements BeforeEachCallback, ParameterResolver {

    private PetApi petApi;

    @Override
    public void beforeEach(ExtensionContext context) {
        petApi = new PetApi();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getType() == PetApi.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == PetApi.class) {
            return petApi;
        }
        return null;
    }
}