package hello.aop.member.internalcall;

import hello.aop.internalcall.CallServiceV0;
import hello.aop.internalcall.CallServiceV3;
import hello.aop.internalcall.aop.CallLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV3Test {

    @Autowired
    CallServiceV3 callServiceV3;


    @Test
    void external() {
        callServiceV3.external();

    }
}