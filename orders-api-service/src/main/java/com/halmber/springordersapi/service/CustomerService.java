package com.halmber.springordersapi.service;

import com.halmber.springordersapi.model.dto.request.customer.CustomerCreateDto;
import com.halmber.springordersapi.model.dto.request.customer.CustomerEditDto;
import com.halmber.springordersapi.model.dto.response.customer.CustomerListResponseDto;
import com.halmber.springordersapi.model.dto.response.customer.CustomerResponseDto;
import com.halmber.springordersapi.model.entity.Customer;
import com.halmber.springordersapi.model.mapper.CustomerMapper;
import com.halmber.springordersapi.repository.BaseRepository;
import com.halmber.springordersapi.repository.CustomerRepository;
import com.halmber.springordersapi.service.exeption.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing customer entities.
 * Provides business logic for creating, reading, updating, and listing customers
 * with proper validation and error handling.
 *
 * <p>Support:
 * <ul>
 *   <li>Paginated customer listing with sorting support</li>
 *   <li>Email uniqueness validation</li>
 *   <li>Transactional operations for data consistency</li>
 *   <li>DTO-based data transfer for API layer</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService extends BaseService<Customer, UUID> {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final EmailMessageProducerService emailMessageProducerService;

    @Override
    protected BaseRepository<Customer, UUID> getRepository() {
        return repository;
    }
    
    @Transactional(readOnly = true)
    public CustomerListResponseDto listCustomers(Pageable pageable) {
        Page<Customer> page = repository.findAll(pageable);

        return CustomerListResponseDto.builder()
                .customers(mapper.toList(page.toList()))
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getById(UUID id) {
        return mapper.toResponse(findByIdOrThrow(id));
    }

    @Transactional
    public CustomerResponseDto create(CustomerCreateDto customer) {
        Customer entity = mapper.toEntity(customer);

        if (repository.existsByEmail(entity.getEmail())) {
            throw new AlreadyExistsException(Customer.class.getSimpleName(), "email", entity.getEmail());
        }

        Customer savedCustomer = repository.save(entity);

        // Send email notification with generated ID based on customer ID
        String emailId = "customer-welcome-" + savedCustomer.getId();
        String subject = "Welcome to Orders API!";
        String content = String.format(
            """
            Hello %s %s,
            
            Welcome to our Orders API platform!
            Your account has been successfully created.
            
            Account Details:
            Name: %s %s
            Email: %s
            City: %s
            
            Best regards,
            Halmber""",
            savedCustomer.getFirstName(),
            savedCustomer.getLastName(),
            savedCustomer.getFirstName(),
            savedCustomer.getLastName(),
            savedCustomer.getEmail(),
            savedCustomer.getCity()
        );

        emailMessageProducerService.sendEmailNotification(
                emailId,
                savedCustomer.getEmail(),
                subject,
                content
        );

        return mapper.toResponse(savedCustomer);
    }

    @Transactional
    public CustomerResponseDto update(UUID id, CustomerEditDto dto) {
        Customer entity = findByIdOrThrow(id);

        mapper.updateEntityFromDto(dto, entity);

        return mapper.toResponse(repository.save(entity));
    }
}
