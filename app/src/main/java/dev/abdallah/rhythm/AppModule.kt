package dev.abdallah.rhythm

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.abdallah.rhythm.data.db.AppDatabase
import dev.abdallah.rhythm.data.db.PlaylistDao
import dev.abdallah.rhythm.data.db.PlaylistSongDao
import dev.abdallah.rhythm.data.db.SongDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "room_database"
        ).build()
    }
    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase) : SongDao {
        return appDatabase.songDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(appDatabase: AppDatabase) : PlaylistDao {
        return appDatabase.playlistDao()
    }

    @Provides
    @Singleton
    fun providePlaylistSongDao(appDatabase: AppDatabase) : PlaylistSongDao {
        return appDatabase.playlistSongDao()
    }
}
