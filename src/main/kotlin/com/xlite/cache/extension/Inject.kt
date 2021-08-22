package com.xlite.cache.extension

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent

inline fun <reified T : Any> inject(
    qualifier: Qualifier? = null, noinline parameters: ParametersDefinition? = null
): Lazy<T> = KoinJavaComponent.getKoin().inject(qualifier, parameters = parameters)