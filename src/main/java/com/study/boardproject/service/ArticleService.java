package com.study.boardproject.service;

import com.study.boardproject.domain.Article;
import com.study.boardproject.domain.type.SearchType;
import com.study.boardproject.dto.ArticleDto;
import com.study.boardproject.dto.ArticleWithCommentsDto;
import com.study.boardproject.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;


    // 전체 조회
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            // service는 ArticleDto까지만 알아야 한다. -> entity를 map함수로 dto로 변환시킨다.
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        //enum 은 switch가 편하다. 이제 switch문을 return할수가 있다.
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    // 단건조회
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from) //articleId를 노출할지는 고민해봐라
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    // 저장
    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    // 수정 transaction이 묶여있어서 save를 작성안해줘도 알아서 업데이트를 한다. 더티체킹
    public void updateArticle(ArticleDto dto) {
        // getReferenceById로 select쿼리를 안날리도록 최적화 한다.
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            if (dto.title() != null) { article.setTitle(dto.title()); }
            if (dto.content() != null) { article.setContent(dto.content()); }
            article.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다. -dto: {}", dto);
        }

    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if (hashtag == null || hashtag.isBlank()) {
            return Page.empty(); // 빈 페이지를 보여준다.
        }
        return articleRepository.findByHashtag(hashtag, pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }
}
