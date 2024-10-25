package hello.aop.member.internalcall;

import hello.aop.internalcall.CallServiceV0;
import hello.aop.internalcall.aop.CallLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV1Test {

    @Autowired
    CallServiceV0 callServiceV0;


    @Test
    void external() {
        callServiceV0.external();

    }

    @Test
    void internal() {
        callServiceV0.internal();
    }
}