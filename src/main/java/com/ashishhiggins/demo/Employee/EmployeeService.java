package com.ashishhiggins.demo.Employee;

import com.ashishhiggins.demo.Email.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmailService emailService;




    public EmployeeService(EmployeeRepository employeeRepository, EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
    }

    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    public ResponseEntity<Employee> createEmployee(Employee employee) {
       employee.setId(UUID.randomUUID().toString());

        employeeRepository.save(employee);

        String level1ManagerEmail = getLevel1ManagerEmail(employee);

        // Send email to level 1 manager
        emailService.sendEmailToManager(level1ManagerEmail, employee);

        return ResponseEntity.status(201).body(employee);
    }

    public ResponseEntity<Void> deleteById(String id) {
        employeeRepository.deleteById(id);
        return ResponseEntity.status(200).build();
    }

    public ResponseEntity<Employee> updateEmployee( String id, Employee employee) {
        Optional<Employee> existingEmployeeOptional = employeeRepository.findById(id);
        if (existingEmployeeOptional.isPresent()) {
            Employee existingEmployee = existingEmployeeOptional.get();
            existingEmployee.setEmployeeName(employee.getEmployeeName());
            existingEmployee.setPhoneNumber(employee.getPhoneNumber());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setReportsTo(employee.getReportsTo());
            existingEmployee.setProfileImage(employee.getProfileImage());
            employeeRepository.save(existingEmployee);
            return ResponseEntity.ok(existingEmployee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    public Optional<Employee> getNthLevelManager(String employeeId, int level) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            if (level == 0) {
                return Optional.of(employee);
            } else {
                return getNthLevelManager(employee.getReportsTo(), level - 1);
            }
        } else {
            return Optional.empty();
        }
    }

    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }


    public String getLevel1ManagerEmail(Employee employee) {
        if (employee.getReportsTo() != null) {
            String level1ManagerId = getLevel1ManagerId(employee.getReportsTo());
            if (level1ManagerId != null) {
                Optional<Employee> level1ManagerOptional = employeeRepository.findById(level1ManagerId);
                if (level1ManagerOptional.isPresent()) {
                    return level1ManagerOptional.get().getEmail();
                }
            }
        }
        // Return a default email or throw an exception if level 1 manager is not found
        return "default@example.com";
    }

    private String getLevel1ManagerId(String managerId) {
        Optional<Employee> managerOptional = employeeRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            Employee manager = managerOptional.get();
            if (manager.getReportsTo() != null) {
// If the manager has a reporting manager, get the reporting manager's id


                return manager.getId();
            } else {
                // If the manager doesn't have a reporting manager, it's a level 1 manager
                return null;
            }
        }
        return null;
    }

}
