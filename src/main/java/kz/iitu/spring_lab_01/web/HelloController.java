package kz.iitu.spring_lab_01.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${app.owner:unknown}")
    private String owner;

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(defaultValue = "world") String name) {
        return new Greeting("Hello, " + name + "!", owner, LocalDateTime.now());
    }

    @GetMapping("/info")
    public Info info() {
        return new Info(owner,
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors());
    }

    // Индивидуальное задание — вариант 1: сумма, разность, произведение
    @GetMapping("/sum")
    public SumResult sum(@RequestParam(required = false) Integer a,
                         @RequestParam(required = false) Integer b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Both parameters 'a' and 'b' are required");
        }
        return new SumResult(a, b, a + b, a - b, a * b);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    public record Greeting(String message, String owner, LocalDateTime timestamp) { }

    public record Info(String owner, String javaVersion, int cpuCores) { }

    public record SumResult(int a, int b, int sum, int difference, int product) { }

    public record ErrorResponse(String error) { }
}