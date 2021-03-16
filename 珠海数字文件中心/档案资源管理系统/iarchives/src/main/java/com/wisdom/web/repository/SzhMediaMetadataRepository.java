package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_media_metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SzhMediaMetadataRepository extends JpaRepository<Szh_media_metadata, String>,JpaSpecificationExecutor<Szh_media_metadata> {
    Szh_media_metadata findByMediaid(String mediaId);
    Integer deleteByIdIn(String[] ids);
    Integer deleteByArchivecodeIn(String[] archivecodes);
    List<Szh_media_metadata> findByArchivecode(String archivecode);
}
