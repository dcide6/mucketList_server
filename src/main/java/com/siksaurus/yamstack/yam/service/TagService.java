package com.siksaurus.yamstack.yam.service;

import com.siksaurus.yamstack.yam.domain.Tag;
import com.siksaurus.yamstack.yam.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Optional<Tag> getTagByName(String name) {
        return this.tagRepository.findByName(name);
    }

    public Tag saveTag(Tag tag) {
        return this.tagRepository.save(tag);
    }

    public Set<Tag> saveTags(Set<String> tags) {
        if(tags == null) return new HashSet<>();
        Set<Tag> tagSet = new HashSet<>();
        tags.forEach(t -> {
            Tag tag = this.getTagByName(t).orElse(Tag.builder().name(t).build());
            tagSet.add(tag);
        });
        try{
            List<Tag> list = this.tagRepository.saveAll(tagSet);
            Set<Tag> set = new HashSet<>(list);
            return set;
        } catch (Exception e) {
            log.error("saveTags error :"+e.getMessage());
            return new HashSet<>();
        }
    }
}
