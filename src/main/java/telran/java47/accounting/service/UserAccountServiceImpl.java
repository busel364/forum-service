package telran.java47.accounting.service;

import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {

	final UserAccountRepository userAccountRepository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsExeption();
		}
		UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
		String password = passwordEncoder.encode(userRegisterDto.getPassword());
//		String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
		userAccount.setPassword(password);
		userAccount.setChangePasswordDate(LocalDate.now().plusDays(60));
		userAccount.addRole("USER");
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
		if (userEditDto.getFirstName() != null) {
			userAccount.setFirstName(userEditDto.getFirstName());
		}
		if (userEditDto.getLastName() != null) {
			userAccount.setLastName(userEditDto.getLastName());
		}
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
		String password = passwordEncoder.encode(newPassword);
		userAccount.setPassword(password);
		userAccount.setChangePasswordDate(LocalDate.now().plusDays(60));
		userAccountRepository.save(userAccount);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!userAccountRepository.existsById("admin")) {
			String password = BCrypt.hashpw("admin", BCrypt.gensalt());
			UserAccount userAccount = new UserAccount("admin", password, "", "");
			userAccount.addRole("USER");
			userAccount.addRole("MODERATOR");
			userAccount.addRole("ADMINISTRATOR");
			userAccountRepository.save(userAccount);
		}
	}

}
