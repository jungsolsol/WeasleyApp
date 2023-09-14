package sol.server.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String uuid;

    private String uuidPw;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<User> user = new ArrayList<>();

    public Product(String uuid, String uuidPw) {
        this.uuid = uuid;
        this.uuidPw = uuidPw;
    }

    // Builder 패턴 추가
    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private String uuid;
        private String uuidPw;

        public ProductBuilder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public ProductBuilder uuidPw(String uuidPw) {
            this.uuidPw = uuidPw;
            return this;
        }

        public Product build() {
            return new Product(uuid,uuidPw);
        }
    }
}