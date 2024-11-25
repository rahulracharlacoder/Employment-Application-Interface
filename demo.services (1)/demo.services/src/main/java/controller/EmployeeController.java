package controller;
import configuration.JwtConfig;
import employeedto.EmployeeRequest;
import exception.ResourceNotFoundException;
import io.jsonwebtoken.Claims;

import entity.Employee;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.EmployeeService;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequest employeeRequest, @RequestHeader("Authorization") String token) {
        if (isTokenInvalid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Token Expired or Invalid");
        }
        employeeService.addEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Employee Added");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (isTokenInvalid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Token Expired or Invalid");
        }

        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee Not Found");
        }
    }

    private boolean isTokenInvalid(String token) {
        if (token == null || token.isEmpty()) {
            return true;
        }

        try {
            Claims claims = decodeToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims decodeToken(String token) throws Exception {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}