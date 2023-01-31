package com.friendzrandroid.home.di

import com.friendzrandroid.home.data.datasource.*
import com.friendzrandroid.home.data.repository.FaceComparingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeApiModule {


    @Singleton
    @Provides
    fun provideFeedApis(retrofit: Retrofit): FeedRequestAPIS {
        return retrofit
            .create(FeedRequestAPIS::class.java)
    }


    @Singleton
    @Provides
    fun provideAccountApis(retrofit: Retrofit): AccountAPIS {
        return retrofit
            .create(AccountAPIS::class.java)
    }


    @Singleton
    @Provides
    fun provideEventApis(retrofit: Retrofit): EventAPIS {
        return retrofit
            .create(EventAPIS::class.java)
    }


    @Singleton
    @Provides
    fun provideMapApis(retrofit: Retrofit): MapAPIS {
        return retrofit
            .create(MapAPIS::class.java)
    }

    @Singleton
    @Provides
    fun provideMessageApis(retrofit: Retrofit): MessageAPIS {
        return retrofit
            .create(MessageAPIS::class.java)
    }

    @Singleton
    @Provides
    fun provideReportsApis(retrofit: Retrofit): ReportAPIS {
        return retrofit
            .create(ReportAPIS::class.java)
    }


    @Singleton
    @Provides
    fun provideGroupApis(retrofit: Retrofit): GroupAPIS {
        return retrofit
            .create(GroupAPIS::class.java)
    }

    @Singleton
    @Provides
    fun provideCommunityApis(retrofit: Retrofit): CommunityAPIS {
        return retrofit
            .create(CommunityAPIS::class.java)
    }

    @Singleton
    @Provides
    fun provideFaceDetectingRepo(): FaceComparingRepository {
        return FaceComparingRepository()
    }


}