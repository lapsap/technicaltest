package com.tecforte.blog.service;

import com.tecforte.blog.domain.Entry;
import com.tecforte.blog.repository.EntryRepository;
import com.tecforte.blog.service.dto.EntryDTO;
import com.tecforte.blog.service.mapper.EntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Implementation for managing {@link Entry}.
 */
@Service
@Transactional
public class EntryService {

    private final Logger log = LoggerFactory.getLogger(EntryService.class);

    private final EntryRepository entryRepository;

    private final EntryMapper entryMapper;

    public EntryService(EntryRepository entryRepository, EntryMapper entryMapper) {
        this.entryRepository = entryRepository;
        this.entryMapper = entryMapper;
    }

    /**
     * Save a entry.
     *
     * @param entryDTO the entity to save.
     * @return the persisted entity.
     */
    public EntryDTO save(EntryDTO entryDTO) {
        log.debug("Request to save Entry : {}", entryDTO);
        Entry entry = entryMapper.toEntity(entryDTO);
        entry = entryRepository.save(entry);
        return entryMapper.toDto(entry);
    }

    /**
     * Get all the entries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EntryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Entries");
        return entryRepository.findAll(pageable)
            .map(entryMapper::toDto);
    }


    /**
     * Get one entry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EntryDTO> findOne(Long id) {
        log.debug("Request to get Entry : {}", id);
        return entryRepository.findById(id)
            .map(entryMapper::toDto);
    }

    /**
     * Delete the entry by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Entry : {}", id);
        entryRepository.deleteById(id);
    }

    // function to check string if contains a word
    private static boolean isContain(String source, String subItem){
        String tmp = "";
        for (int i =0; i<source.length(); i++) {
            if (source.charAt(i) == ' '){
                if (tmp.equals(subItem))
                    return true;
                tmp = "";
            } else {
                tmp += source.charAt(i);
            }
        }
        if (tmp.equals(subItem))
            return true;
        return false;
    }

    public void clean(String keyword) {
        Pageable wholePage = Pageable.unpaged();
        Page<EntryDTO> allentries = entryRepository.findAll(wholePage)
            .map(entryMapper::toDto);
        log.debug("hehe {}", allentries.getContent());
        
        List<EntryDTO> allData = allentries.getContent();
        for (int i = 0; i < allData.size(); i++) {
            EntryDTO tmp = allData.get(i);
            if (isContain(tmp.getTitle().toLowerCase(), keyword) || isContain(tmp.getContent().toLowerCase(), keyword)){
                entryRepository.deleteById(tmp.getId());
            }
        }
    }

    public void cleanwithid(String keyword, int blogid) {
        Pageable wholePage = Pageable.unpaged();
        Page<EntryDTO> allentries = entryRepository.findAll(wholePage)
            .map(entryMapper::toDto);
        log.debug("hehe {}", allentries.getContent());
        
        List<EntryDTO> allData = allentries.getContent();
        for (int i = 0; i < allData.size(); i++) {
            EntryDTO tmp = allData.get(i);
            if (tmp.getBlogId() == blogid) {
                if (isContain(tmp.getTitle().toLowerCase(), keyword) || isContain(tmp.getContent().toLowerCase(), keyword)){
                    entryRepository.deleteById(tmp.getId());
                }
            }
        }
    }

}
