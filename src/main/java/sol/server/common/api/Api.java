package sol.server.common.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import sol.server.common.error.ErrorCodeIfs;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Api<T> {

    private Result result;

    private String token;
    private HttpHeaders headers;

    @Valid
    private T body;

    public static <T> Api<T> OK(T data){
        var api = new Api<T>();
        api.result = Result.OK();
        api.body = data;
        return api;
    }


    public static <T> Api<T> OK(T data, String token){
        var api = new Api<T>();
        api.result = Result.OK();
        api.body = data;
        api.token = token; // 토큰 설정
        api.headers = new HttpHeaders();
        return api;
    }


    public static Api<Object> ERROR(Result result){
        var api = new Api<Object>();
        api.result = result;
        return api;
    }


    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs);
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs, Throwable tx){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs, tx);
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs, String description){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs, description);
        return api;
    }

    public void addHeaders(HttpHeaders newHeaders) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.addAll(newHeaders);
    }
}
