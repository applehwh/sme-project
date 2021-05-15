import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class RequestAuthFilter implements GlobalFilter, Ordered {

    /**
     * 请求方式验证过滤器
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * 作者:will
     * 日期:2019/4/4 14:46
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String method = serverHttpRequest.getMethodValue();
        if(!"POST".equals(method)){
            ServerHttpResponse response = exchange.getResponse();
            String message= "非法请求";// new ResponseUtils().CreditRespMsg(CreditException.ERR_100008,"非法请求",null);
            byte[] bits = message.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    /**
     * 优先级
     * @return int 数字越大优先级越低
     * 作者:will
     * 日期:2019/4/4 13:36
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
