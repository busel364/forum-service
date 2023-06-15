package telran.java47.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java47.post.dao.PostRepository;
import telran.java47.post.dto.DatePeriodDto;
import telran.java47.post.dto.NewCommentDto;
import telran.java47.post.dto.NewPostDto;
import telran.java47.post.dto.PostDto;
import telran.java47.post.dto.exeptions.PostNotFoundExeption;
import telran.java47.post.model.Comment;
import telran.java47.post.model.Post;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	final PostRepository postRepository;
	final ModelMapper modelMapper;

	@Override
	public PostDto addNewPost(String author, NewPostDto newPostDto) {
		Post post = modelMapper.map(newPostDto, Post.class);
		post.setAuthor(author);
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto findPostById(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundExeption());
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto removePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundExeption());
		postRepository.deleteById(id);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto updatePost(String id, NewPostDto newPostDto) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundExeption());
		newPostDto.getTags().forEach((tag) -> post.addTag(tag));
		post.setContent(newPostDto.getContent());
		post.setTitle(newPostDto.getTitle());
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundExeption());
		Comment comment = new Comment(author, newCommentDto.getMessage());
		post.addComment(comment);
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);

	}

	@Override
	public void addLike(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundExeption());
		post.addLike();
		postRepository.save(post);
	}

	@Override
	public Iterable<PostDto> findPostByAuthor(String author) {
		return postRepository.findByAuthorIgnoreCase(author).map(post -> modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PostDto> findPostsByTags(List<String> tags) {
		return postRepository.findByTagsInIgnoreCase(tags).map(post -> modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
		return postRepository.findByDateCreatedBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
				.map(post -> modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
	}

}
