package com.luv2code.springboot.cruddemo.rest;

import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private JsonMapper jsonMapper;
    private EmployeeService employeeService;

    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService,JsonMapper theJsonMapper){
        jsonMapper = theJsonMapper;
        employeeService = theEmployeeService;
    }
    //expose "/employees" endpoint and return a list of all employees
    @GetMapping("/employees")
    public List<Employee> findAll(){
        return employeeService.findAll();
    }

    //add  mapping for GET /employees/{employeeId}
    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId){
        Employee theEmployee = employeeService.findById(employeeId);
        if(theEmployee == null) {
            throw new RuntimeException("Employee id not found: " + employeeId);
        }
        return theEmployee;
    }

    //add mapping for Post "/employees"
    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee){
        // also just in case they pass an id in JSON ... set id to 0
        //this is to force a save of a new item ... instead of update.
        theEmployee.setId(0);
        Employee dbEmployee = employeeService.save(theEmployee);
        return dbEmployee;
    }

    //add mapping for PUT/ employees -update existing employee
    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee){
        Employee dbEmployee = employeeService.save(theEmployee);
        return dbEmployee;
    }

    // add mapping for  PATCH
    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId, @RequestBody Map<String, Object> patchPayload){
        Employee tempEmployee = employeeService.findById(employeeId);

        //throw exception if null
        if(tempEmployee == null){
            throw new RuntimeException("employee not found id: " + employeeId);
        }

        // throw exception if request body contains 'id' key CANNOT CHANGE ID
        if(patchPayload.containsKey("id")){
            throw new RuntimeException("Employee id is not allowed in the request body, " + employeeId);
        }

        Employee patchedEmployee = jsonMapper.updateValue(tempEmployee,patchPayload);

        Employee dbEmployee = employeeService.save(patchedEmployee);

        return dbEmployee;
    }

    //add mapping for DELETE/employees -delete existing employee
    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId){

        Employee tempEmployee = employeeService.findById(employeeId);
        //throw exception message if not found
        if(tempEmployee == null){
            throw new RuntimeException("Employee id not found - " +employeeId);
        }

        employeeService.deleteById(employeeId);
        return "deleted employee " + employeeId;
    }


}
