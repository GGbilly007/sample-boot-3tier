package th.mfu.service.repository;

import org.springframework.data.repository.CrudRepository;

import th.mfu.domain.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

}
