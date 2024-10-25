package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV2 {
    private CallServiceV2 callServiceV2;
    private ObjectProvider<?> objectProvider;

    public CallServiceV2(ObjectProvider<?> objectProvider) {
        this.objectProvider = objectProvider;
    }

    public void external() {
        log.info("call external");
        CallServiceV2 callServiceV2 = (CallServiceV2) objectProvider.getObject();
        callServiceV2.internal(); //내부 메서드 호출(this.internal())
    }

    public void internal() {
        log.info("call internal");
    }
}
