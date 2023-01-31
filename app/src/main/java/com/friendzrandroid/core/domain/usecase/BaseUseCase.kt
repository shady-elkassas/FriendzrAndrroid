package com.friendzrandroid.core.domain.usecase

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<in Params,out Type> {
    abstract  fun execute(params:Params): Flow<Type>
}