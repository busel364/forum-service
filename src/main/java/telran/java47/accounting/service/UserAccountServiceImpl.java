package telran.java47.accounting.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.dto.RolesDto;
import telran.java47.accounting.dto.UserDto;
import telran.java47.accounting.dto.UserEditDto;
import telran.java47.accounting.dto.UserRegisterDto;
import telran.java47.accounting.dto.exeptions.UserExistsExeption;
import telran.java47.accounting.dto.exeptions.UserNotFoundExeption;
import telran.java47.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

	final UserAccountRepository userAccountRepository;
	final ModelMapper modelMapper;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsExeption();
		}
		UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
		userAccount.addRole("User");
		userAccountRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userAccountRepository.delete(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto userEditDto) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userAccount.setFirstName(userEditDto.getFirstName());
		userAccount.setLastName(userEditDto.getLastName());
		userAccountRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		if (isAddRole) {
			userAccount.addRole(role);
		} else {
			userAccount.removeRole(role);
		}
		userAccountRepository.save(userAccount);
		return modelMapper.map(userAccount, RolesDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userAccount.setPassword(newPassword);
		userAccountRepository.save(userAccount);
	}

}
