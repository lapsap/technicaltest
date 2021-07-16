package com.tecforte.blog.web.rest;

import com.tecforte.blog.service.EntryService;
import com.tecforte.blog.web.rest.errors.BadRequestAlertException;
import com.tecforte.blog.service.dto.EntryDTO;
import com.tecforte.blog.service.dto.BlogDTO;
import com.tecforte.blog.service.BlogService;
import com.tecforte.blog.domain.enumeration.Emoji;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.lang.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.tecforte.blog.domain.Entry}.
 */
@RestController
@RequestMapping("/api")
public class EntryResource {

    private final Logger log = LoggerFactory.getLogger(EntryResource.class);

    private static final String ENTITY_NAME = "entry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntryService entryService;
    private final BlogService blogService;

    public EntryResource(EntryService entryService, BlogService blogService) {
        this.entryService = entryService;
        this.blogService = blogService;
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

    /**
     * {@code POST  /entries} : Create a new entry.
     *
     * @param entryDTO the entryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entryDTO, or with status {@code 400 (Bad Request)} if the entry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/entries")
    public ResponseEntity<EntryDTO> createEntry(@Valid @RequestBody EntryDTO entryDTO) throws URISyntaxException {
        log.debug("REST request to save Entry : {}", entryDTO);
        if (entryDTO.getId() != null) {
            throw new BadRequestAlertException("A new entry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<BlogDTO> blogDTO = blogService.findOne(entryDTO.getBlogId());
        blogDTO.ifPresent(dto -> {
            if (dto.isPositive()) {
                // part 1.1
                if (entryDTO.getEmoji() == Emoji.SAD || entryDTO.getEmoji() == Emoji.ANGRY) {
                    throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
                }
                // part 1.2
                if (isContain(entryDTO.getTitle().toLowerCase() ,"sad") || isContain(entryDTO.getContent().toLowerCase(), "sad") ||
                isContain(entryDTO.getTitle().toLowerCase() ,"fear") || isContain(entryDTO.getContent().toLowerCase(), "fear") ||
                isContain(entryDTO.getTitle().toLowerCase() ,"lonely") || isContain(entryDTO.getContent().toLowerCase(), "lonely")
                ){
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                }
            } else {
                // part 1.1
                if (entryDTO.getEmoji() == Emoji.LIKE || entryDTO.getEmoji() == Emoji.HAHA) {
                    throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
                }
                // part 1.2
                if (isContain(entryDTO.getTitle().toLowerCase() ,"love") || isContain(entryDTO.getContent().toLowerCase(), "love") ||
                isContain(entryDTO.getTitle().toLowerCase() ,"happy") || isContain(entryDTO.getContent().toLowerCase(), "happy") ||
                isContain(entryDTO.getTitle().toLowerCase() ,"trust") || isContain(entryDTO.getContent().toLowerCase(), "trust")
                ){
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                }
            }
        });
        EntryDTO result = entryService.save(entryDTO);
        return ResponseEntity.created(new URI("/api/entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /entries} : Updates an existing entry.
     *
     * @param entryDTO the entryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entryDTO,
     * or with status {@code 400 (Bad Request)} if the entryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/entries")
    public ResponseEntity<EntryDTO> updateEntry(@Valid @RequestBody EntryDTO entryDTO) throws URISyntaxException {
        log.debug("REST request to update Entry : {}", entryDTO);
        if (entryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EntryDTO result = entryService.save(entryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /entries} : get all the entries.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entries in body.
     */
    @GetMapping("/entries")
    public ResponseEntity<List<EntryDTO>> getAllEntries(Pageable pageable) {
        log.debug("REST request to get a page of Entries");
        Page<EntryDTO> page = entryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /entries/:id} : get the "id" entry.
     *
     * @param id the id of the entryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/entries/{id}")
    public ResponseEntity<EntryDTO> getEntry(@PathVariable Long id) {
        log.debug("REST request to get Entry : {}", id);
        Optional<EntryDTO> entryDTO = entryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(entryDTO);
    }

    /**
     * {@code DELETE  /entries/:id} : delete the "id" entry.
     *
     * @param id the id of the entryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        log.debug("REST request to delete Entry : {}", id);
        entryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
