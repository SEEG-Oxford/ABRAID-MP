package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper;

import org.kubek2k.springockito.annotations.internal.Loader;
import org.springframework.test.context.web.GenericXmlWebContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * A SpringockitoContextLoader that works with @WebAppConfiguration.
 * Taken from Springockito Pull Request 6 by Andrew Chen.
 * https://bitbucket.org/kubek2k/springockito/pull-request/6
 *
 * Springockito is distributed under MIT license Copyright (c) 2011 Jakub Janczak.
 *
 * Should be removed when then next version of Springockito is released.
 */
public class SpringockitoWebContextLoader extends GenericXmlWebContextLoader {
    private Loader loader = new Loader();

    @Override
    protected void customizeContext(GenericWebApplicationContext context, WebMergedContextConfiguration webMergedConfig) {
        super.customizeContext(context, webMergedConfig);
        loader.registerMocksAndSpies(context);
    }

    @Override
    protected String[] generateDefaultLocations(Class<?> clazz) {
        String[] resultingLocations = super.generateDefaultLocations(clazz);
        loader.defineMocksAndSpies(clazz);
        return resultingLocations;
    }

    @Override
    protected String[] modifyLocations(Class<?> clazz, String... passedLocations) {
        String[] resultingLocations = super.modifyLocations(clazz, passedLocations);
        loader.defineMocksAndSpies(clazz);
        return resultingLocations;
    }
}
