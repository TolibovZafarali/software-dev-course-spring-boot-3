package com.example.springBoot2.Repositories;

import com.example.springBoot2.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
}
