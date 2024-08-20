package com.anhduc.mevabe.service;

import com.anhduc.mevabe.entity.Article;
import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.repository.ArticleRepository;
import com.anhduc.mevabe.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArticleService {

    ArticleRepository articleRepository;

    public void create(Article article) {
        articleRepository.save(article);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public void delete(UUID id) {
        articleRepository.deleteById(id);
    }

    public Article findById(UUID id) {
        return articleRepository.findById(id).orElse(null);
    }

}
