package telran.java47.accounting.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java47.accounting.model.UserAccount;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
	
}
