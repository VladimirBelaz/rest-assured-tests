package extensions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import services.ActivityApi;
import services.BookApi;

import java.lang.reflect.Parameter;

public class ApiServiceExtension implements BeforeEachCallback, ParameterResolver {

    private ActivityApi activityApi;
    private BookApi bookApi;

    @Override
    public void beforeEach(ExtensionContext context) {
        activityApi = new ActivityApi();
        bookApi = new BookApi();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getType() == ActivityApi.class || parameter.getType() == BookApi.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == ActivityApi.class) {
            return activityApi;
        }
        if (type == BookApi.class) {
            return bookApi;
        }
        return null;
    }
}