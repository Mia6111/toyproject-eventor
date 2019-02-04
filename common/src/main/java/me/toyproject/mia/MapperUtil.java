package me.toyproject.mia;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MapperUtil {
    @Autowired
    private MapperFacade orikaMapperFacade;

    public <T,R> R createDto(T t, Class<R> targetClass){
        return orikaMapperFacade.map(t, targetClass);
    }

    public <T,R> List<R> createDtoList(List<T> t, Class<R> targetClass){
        return orikaMapperFacade.mapAsList(t, targetClass);
    }
}
