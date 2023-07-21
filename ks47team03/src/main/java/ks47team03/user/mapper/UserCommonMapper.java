package ks47team03.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ks47team03.user.dto.User;

@Mapper
public interface UserCommonMapper {
	
	// 회원가입
	public int joinUser(User user);

	// 회원 상세 조회
	public User getUserInfoById(String userId);
	
	// 회원등급 조회
	public List<User> getUserLevelList();
	
	// 회원 중복 체크
	public boolean idCheck(String userId);

}