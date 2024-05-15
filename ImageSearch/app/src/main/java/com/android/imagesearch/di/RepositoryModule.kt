package com.android.imagesearch.di

import com.android.imagesearch.repository.ImageSearchRepository
import com.android.imagesearch.repository.ImageSearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindImageSearchRepository(
        imageSearchRepositoryImpl: ImageSearchRepositoryImpl
    ) : ImageSearchRepository
}