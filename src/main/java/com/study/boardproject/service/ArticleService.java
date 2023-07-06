package com.study.boardproject.service;

import com.study.boardproject.domain.Article;
import com.study.boardproject.domain.Hashtag;
import com.study.boardproject.domain.UserAccount;
import com.study.boardproject.domain.type.SearchType;
import com.study.boardproject.dto.ArticleDto;
import com.study.boardproject.dto.ArticleWithCommentsDto;
import com.study.boardproject.repository.ArticleRepository;
import com.study.boardproject.repository.HashtagRepository;
import com.study.boardproject.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final HashtagService hashtagService;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        // 새로운 방식의 switch문 -> 바로 return이 가능하다.
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagNames(
                            Arrays.stream(searchKeyword.split(" ")).toList(),
                            pageable
                    )
                    .map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    /**
     * 게시글 저장
     */
    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        // parsing한 결과를 받아야 한다.
        Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());

        // dto -> entity로 전환
        Article article = dto.toEntity(userAccount);
        article.addHashtags(hashtags);

        articleRepository.save(article);
    }

    /**
     * 게시글 수정
     */
    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if (article.getUserAccount().equals(userAccount)) {
                if (dto.title() != null) { article.setTitle(dto.title()); }
                if (dto.content() != null) { article.setContent(dto.content()); }
            }

            // 게시글로부터 해시태그 id을 추출하여 set을 만든다.
            Set<Long> hashtagIds = article.getHashtags().stream()
                    .map(Hashtag::getId)
                    .collect(Collectors.toUnmodifiableSet());

            // 게시글에 있는 해시태그를 한번 지워준다.
            article.clearHashtags();
            articleRepository.flush();

            hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);

            // 새롭게 parsing해서 넣는다.
            Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
            article.addHashtags(hashtags);

        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    /**
     * 게시글 삭제
     */
    public void deleteArticle(long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        Set<Long> hashtagIds = article.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(Collectors.toUnmodifiableSet());

        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
        articleRepository.flush(); // 게시글을 먼저 삭제한다.

        // 이제 게시글이 없는 해시태그가 판단이 되므로 제거를 한다.
        hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName, Pageable pageable) {
        if (hashtagName == null || hashtagName.isBlank()) {
            return Page.empty(pageable);
        }

        // 해시태그 이름을 여러개 받아서 검색이 가능하도록 한다.
        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        //TODO: HashtagService로 이동을 고려해보자
        return hashtagRepository.findAllHashtagNames();
    }

    // 본문을 받아서 해시태그를 parsing한 결과를 반환받는 메소드
    private Set<Hashtag> renewHashtagsFromContent(String content) {
        // parsing한 결과를 받아낸다.
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        // parsing한 결과로 db에 존재하는지 검색한다.
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);
        // 존재하는 hashtag를 string으로 변환시킨다.
        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());

        // 본문에서 parsing한걸로 새로운 hashtag를 우리 db에있는지 반복해서 찾아서 저장할것 add해서 set으로 만들어서 return 받는다.
        hashtagNamesInContent.forEach(newHashtagName -> {
            if (!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return hashtags;
    }

}