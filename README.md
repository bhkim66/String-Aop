# AOP

- AOP는 부가 기능을 담당하는 코드, 횡단 관심사들을 처리하는 **관점 지향 프로그래밍(Aspect-Oriented Programming)** 이라 한다


### AspectJ 프레임 워크

AOP의 대표적인 구현으로 AspectJ 프레임워크(https://www.eclipse.org/aspectj/)가 있다. 물론 스프링도 AOP를 지원하지만 대부분 AspectJ의 문법을 차용하고, AspectJ가 제공하는 기능의 일부만 제공한다

**스프링 AOP는 런타임 시점(프록시) 방식을 사용한다**

런타임 시점은 컴파일도 다 끝나고, 클래스 로더에 클래스도 다 올라간 뒤 이미 자바가 실행되고 난 다음에 말한다. 자바의 main 메서드가 이미 실행된 다음이다. 따라서 **자바 언어가 제공하는 범위 안에서 부가 기능을 적용해야 한다.** 스프링과 같은 컨테이너의 도움을 받고 프록시와 DI, 빈 포스트 프로세서 같은 개념들을 총 동원해야 한다. 이렇게 하면 최종적으로 프록시를 통해 스프링 빈에 부가 기능을 적용할 수 있다.

**AOP 적용 위치**

- 프록시 방식을 사용하는 스프링 AOP는 메서드 실행 지점에만 AOP를 적용할 수 있다
    - 프록시는 메서드 오버라이딩 개념으로 동작한다. 따라서 생성자나 static 메서드, 필드 값 접근에는 프록시 개념이 적용될 수 없다
    - 프록시를 사용하는 **스프링 AOP의 조인 포인트는 메서드 실행으로 제한**된다
- 프록시 방식을 사용하는 스프링 AOP는 스프링 컨테이너가 관리할 수 있는 **스프링 빈에만 AOP를 적용**할 수 있다

> 스프링은 `AspectJ`의 문법을 차용하고 프록시 방식의 AOP를 적용한다. `AspectJ` 를 직접 사용한 것은 아니다
> 

### AOP 용어 정리

- **조인 포인트(Join point)**
    - 어드바이스가 적용될 수 있는 위치, 메소드 실행, 생성자 호출, 필드 값 접근, static 메서드 접근 같은 프로그램 실행 중 지점
    - 조인 포인트는 추상적인 개념이다. AOP를 적용할 수 있는 모든 지점이라 생각하면 된다
    - 스프링 AOP는 프록시 방식을 사용하므로 조인 포인트는 항상 메소드 실행 지점으로 제한된다
- **포인트컷(Pointcut)**
    - 조인 포인트 중에서 어드바이스가 적용될 위치를 선별하는 기능
    - 주로 AspectJ 표현식을 사용해서 지정
    - 프록시를 사용하는 스프링 AOP는 메서드 실행 지점만 포인트컷으로 선별 가능
- **타겟(Target)**
    - 어드바이스를 받는 객체, 포인트컷으로 결정
- **어드바이스(Advice)**
    - 부가 기능
    - 특정 조인 포인트에서 Aspect에 의해 취해지는 조치
    - Around(주변), Before(전), After(후)와 같은 다양한 종류의 어드바이스가 있음
- **에스팩트(Aspect)**
    - 어드바이스 + 포인트컷을 모듈화 한 것
    - `@Aspect` 스프링은 어노테이션을 제공한다
    - 여러 어드바이스와 포인트 컷이 함께 존재
- **어드바이저(Advisor)**
    - 하나의 어드바이스와 하나의 포인트 컷으로 구성
    - 스프링 AOP에서만 사용되는 특별한 용어
- **위빙(Weaving)**
    - 포인트컷으로 결정한 타켓의 조인 포인트에 어드바이스를 적용하는 것
    - 위빙을 통해 핵심 기능 코드에 영향을 주지 않고 부가 기능을 추가 할 수 있음
    - AOP 적용을 위해 애스펙트를 객체에 연결한 상태
        - 컴파일 타임(AspectJ compiler)
        - 로드 타임
        - 런타임, 스프링 AOP는 런타임, 프록시 방식
- **AOP 프록시**
    - AOP 기능을 구현하기 위해 만든 프록시 객체, 스프링에서 AOP 프록시는 JDK 동적 프록시 또는 CGLIB 프록시이다

### 어드바이스 종류

- `@Around` : 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인 포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능
- `@Before` : 조인 포인트 실행 이전에 실행
- `@AfterReturning` : 조인 포인트가 정상 완료후 실행
- `@AfterThrowing` : 메서드가 예외를 던지는 경우 실행
- `@After` : 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)

```java
@Around("hello.aop.order.aop.Pointcuts.orderAndService()")
public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable
	{
	try {
		//@Before
		log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());
		Object result = joinPoint.proceed();
	
		//@AfterReturning
		log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());
		return result;
	} catch (Exception e) {
		//@AfterThrowing
		log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
		throw e;
	} finally {
		//@After
		log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature());
	}
}

@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(JoinPoint joinPoint) {
	log.info("[before] {}", joinPoint.getSignature());
}

@AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
public void doReturn(JoinPoint joinPoint, Object result) {
	log.info("[return] {} return={}", joinPoint.getSignature(), result);
}

@AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
public void doThrowing(JoinPoint joinPoint, Exception ex) {
	log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
}

@After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
public void doAfter(JoinPoint joinPoint) {
	log.info("[after] {}", joinPoint.getSignature());
}
```

**참고 정보 획득**

모든 어드바이스는 JoinPoint를 첫번째 파라미터에 사용할 수 있다.(생략해도 된다) 단 `@Around` 는 ProceedingJoinPoint를 사용해야 한다

**JoinPoint 인터페이스의 주요 기능**

- `getArgs()` : 메서드 인수를 반환합니다.
- `getThis()` : 프록시 객체를 반환합니다.
- `getTarget()` : 대상 객체를 반환합니다.
- `getSignature()` : 조언되는 메서드에 대한 설명을 반환합니다.
- `toString()` : 조언되는 방법에 대한 유용한 설명을 인쇄합니다.

**ProceedingJoinPoint 인터페이스의 주요 기능**

- `proceed()` : 다음 어드바이스나 타켓을 호출한다.

### @before

조인 포인트 실행 전

```java
@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(JoinPoint joinPoint) {
	log.info("[before] {}", joinPoint.getSignature());
}
```

- `@Around`와 다르게 작업 흐름을 변경할 수는 없다
- `@Around`는 `ProceedingJoinPoint.proceed()`를 호출해야 다음 대상이 호출된다. 만약 호출하지 않으면 다음 대상이 호출되지 않는다. 반면에 `@Before`는 `ProceedingJoinPoint.proceed()` 자체를 사용하지 않는다. 메서드 종료 시 자동으로 다음 타켓이 호출된다. 물론 예외가 발생하면 다음 코드가 호출되지는 않는다

### @AfterReturning

메서드 실행이 정상적으로 반환될 때 실행

```java
@AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
public void doReturn(JoinPoint joinPoint, Object result) {
	log.info("[return] {} return={}", joinPoint.getSignature(), result);
}
```

- `returning` 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다
- `returning` 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
- `@Around`와 다르게 반환되는 객체를 변경할 수는 없다. 반환 객체를 변경하려면 `@Around`를 사용해야 한다. 참고로 반환 객체를 조작할 수 는 있다

### @AfterThrowing

메서드 실행이 예외를 던져서 종료될 때 실행

```java
@AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
public void doThrowing(JoinPoint joinPoint, Exception ex) {
	log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
}
```

- `throwing` 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
- `throwing` 절에 지정된 타입과 맞는 예외를 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)

### @After

- 메서드 실행이 종료되면 실행된다. (finally를 생각하면 된다.)
- 정상 및 예외 반환 조건을 모두 처리한다.
- 일반적으로 리소스를 해제하는 데 사용한다.

### @Around

- 메서드의 실행의 주변에서 실행된다. 메서드의 실행 전후에 작업을 수행한다
- 가장 강력한 어드바이스
    - 조인 포인트 실행 여부 선택 `joinpoint.proceed()` 호출 여부 선택
    - 전달 값 변환 : `joinpoint.proceed(args[]))`
    - 반환 값 변환
    - 예외 변환
    - 트랜잭션 처럼 try ~ catch ~ finally 모두 들어가는 구문 처리 가능
- 어드바이스의 첫 번째 파라미터는 `ProceedingJoinPoint` 를 사용해야 한다.
- `proceed()` 를 통해 대상을 실행한다.
- `proceed()` 를 여러번 실행할 수도 있음(재시도)

**순서**

- 스프링은 5.2.7 버전부터 동일한 `@Aspect` 안에서 동일한 조인포인트의 우선순위를 정했다.
- 실행 순서: `@Around` , `@Before` , `@After` , `@AfterReturning` , `@AfterThrowing`
- 어드바이스가 적용되는 순서는 이렇게 적용되지만, 호출 순서와 리턴 순서는 반대라는 점을 알아두자.
- 물론 `@Aspect` 안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다. 이 경우 `@Aspect` 를 분리하고 `@Order` 를 적용하자.

```java
@Slf4j
public class AspectV5Order {
	
	@Aspect
	@Order(2)
	public static class LogAspect {
		
		@Around("hello.aop.order.aop.Pointcuts.allOrder()")
		public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
			log.info("[log] {}", joinPoint.getSignature());
		return joinPoint.proceed();
		}
	}
	@Aspect
	@Order(1)
	public static class TxAspect {
		@Around("hello.aop.order.aop.Pointcuts.orderAndService()")
		public Object doTransaction(ProceedingJoinPoint joinPoint) throwsThrowable {
			log.info("[log] {}", joinPoint.getSignature());
		}
	}
}
```

**@Around 외에 다른 어드바이스가 존재하는 이유**

`@Around` 하나만 있어도 모든 기능을 수행할 수 있다. 그런데 다른 어드바이스들이 존재하는 이유는 무엇일까?

- `@Around` 는 항상`joinPoint.proceed()` 를 호출해야 한다. 그렇지 않으면 치명적인 버극 ㅏ발생한다
    - `@Before` 는 `joinPoint.proceed()` 를 호출하는 고민을 하지 않아도 된다.
- 또한 `@Before`이나 `@After`의 경우 코드를 작성한 의도가 명확하게 드러난다는 점이다

**좋은 설계는 제약이 있는 것이다**

> 제약은 실수를 미연에 방지한다. 일종의 가이드 역할을 한다. 만약 `@Around`를 사용했는데, 중간에 다른 개발자가 해당 코드를 수정해서 호출하지 않으면 큰 장애가 발생한다. 처음부터 `@Before` 을 사용했다면 이런 문제가 발생하지 않는다. 제약 덕분에 명확해진다. 다른 개발자도 이 코드를 보고 고민해야 하는 범위가 줄고 코드의 의도도 파악하기 쉽다
> 

## 포인트 컷

### 포인트 컷 지시자

- 포인트컷 표현식은 포인트컷 지시자(Pointcut Designator)로 시작한다 줄여서 PCD라고 한다
- 포인트컷 지시자의 종류
    - `execution` : 메서드 실행 조인 포인트를 매칭한다. 스프링 AOP에서 가장 많이 사용하고 기능도 복잡하다
    - `within` : 특정 타입 내의 조인 포인트를 매칭한다
    - `args` : 인자가 주어진 타입의 인스턴스의 조인 포인트
    - `this` : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
    - `target` : Target 객체(스프링 AOP 프록시가 가리키는 실제 대상)를 대상으로 하는 조인 포인트
    - `@target` : 실행 객체의 클래스에 주어진 타입의 어노테이션이 있는 조인 포인트
    - `@within` : 주어진 어노테이션이 있는 타입 내 조인 포인트
    - `@annotation` : 메서드가 주어진 어노테이션을 가지고 있는 조인 포인트를 매칭
    - `@args` : 전달된 실제 인수의 런타임 타입이 주어진 타입의 어노테이션을 갖는 조인 포인트
    - `bean` : 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다

`AspectJExpressionPointcut`이 바로 포인트컷  표현식을 처리해주는 클래스이다. 여기에 포인트컷 표현식을 지정하면 된다. `AspectJExpressionPointcut` 는 상위에 `Pointcut` 인터페이스를 가진다

`AspectJExpressionPointcut` 에 `pointcut.setExpression` 을 통해서 포인트컷 표현식을 적용할 수
있다.

### execution 문법

```java
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?namepattern(
param-pattern) throws-pattern?)
execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
```

- 메소드 실행 조인 포인틀르 매칭한ㄷ
- ?는 생략할 수 있다
- `*`  같은 패턴을 지정할 수 있다

```java
AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
```

**매칭 조건**

- 접근제어자?: `public`
- 반환타입: `String`
- 선언타입?: `hello.aop.member.MemberServiceImpl`
- 메서드이름: `hello`
- 파라미터: `(String)`
- 예외?: 생략

**가장 많이 생략한 포인트컷**

```java
@Test
void allMatch() {
pointcut.setExpression("execution(* *(..))");
	assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
}
```

**매칭 조건**

- 접근제어자?: 생략
- 반환타입: `*`
- 선언타입?: 생략
- 메서드이름: `*`
- 파라미터: `(..)`
- 예외?: 없음

**패키지에서 `.` , `..` 의 차이**

- `.` : 정확하게 해당 위치의 패키지
- `..` : 해당 위치의 패키지와 그 하위 패키지도 포함

### execution 파라미터 매칭 규칙

- `(String)` : 정확하게 String 타입 파라미터
- `()` : 파라미터가 없어야 한다.
- `(*)` : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
- `(*, *)` : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
- `(..)` : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다. `0..*` 로 이해하면 된다.
- `(String, ..)` : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
    - 예) `(String)` , `(String, Xxx)` , `(String, Xxx, Xxx)` 허용

### within

`within` 지시자는 특정 타입 내의 조인 포인트들로 매칭을 제한한다. 해당 타입이 매칭되면 그 안의 메서드들이 자동으로 매칭된다. `execution` 과 차이점은 정확하게 타입이 맞아야한다. 부보 타입은 지정할 수 없다

### args

- `args` : 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭
- 기본 문법은 `execution`의 `args`부분과 같다

**execution과 args의 차이점**

- `execution` 은 파라미터 타입이 정확하게 매칭되어야 한다. `execution` 은 클래스에 선언된 정보를 기반으로 판단한다.
- `args` 는 부모 타입을 허용한다. `args` 는 실제 넘어온 파라미터 객체 인스턴스를 보고 판단한다.

```java
public void init() throws NoSuchMethodException {
	helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
}

assertThat(pointcut("args(String)")
	.matches(helloMethod, MemberServiceImpl.class)).isTrue();
assertThat(pointcut("args(Object)")
	.matches(helloMethod, MemberServiceImpl.class)).isTrue();
assertThat(pointcut("args()")
	.matches(helloMethod, MemberServiceImpl.class)).isFalse();
```

### @target, @within

**정의**

- `@target` : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
- `@within` : 주어진 애노테이션이 있는 타입 내 조인 포인트

**설명**

`@target` , `@within` 은 다음과 같이 타입에 있는 애노테이션으로 AOP 적용 여부를 판단한다.

- `@target(hello.aop.member.annotation.ClassAop)`
- `@within(hello.aop.member.annotation.ClassAop)`

```java
@ClassAop
class Target{}
```

**@target vs @within**

- `@target` 은 인스턴스의 모든 메서드를 조인 포인트로 적용한다.
- `@within` 은 해당 타입 내에 있는 메서드만 조인 포인트로 적용한다.


### @annotation, @args

**@annotation**

**정의**

- `@annotation` : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭

**설명**

- `@annotation(hello.aop.member.annotation.MethodAop)`

```java
public class MemberServiceImpl {
	@MethodAop("test value")
	public String hello(String param) {
		return "ok";
	}
}
```

```java
@Slf4j
@Aspect
static class AtAnnotationAspect {
	@Around("@annotation(hello.aop.member.annotation.MethodAop)")
	public Object doAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("[@annotation] {}", joinPoint.getSignature());
		return joinPoint.proceed();
	}
}
```

**@args**
**정의**

- `@args` : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트

**설명**

- 전달된 인수의 런타임 타입에 `@Check` 애노테이션이 있는 경우에 매칭한다.
- `@args(test.Check)`

### bean

**정의**

- `bean` : 스프링 전용 포인트컷 지시자, 빈의 이름으로 지정한다

**설명**

- 스프링 빈의 이름으로 AOP 적용 여부를 지정한다. 이것은 스프링에서만 사용할 수 있는 특별한 지시자이다
- `bean(orderService) || bean(*Repository)`
- `*` 과 같은 패턴을 사용할 수 있다

### 매개변수 전달

다음은 포인트컷 표현식을 사용해서 어드바이스에 매개변수를 전달할 수 있다.
**this, target, args,@target, @within, @annotation, @args**

```java
@Before("allMember() && args(arg,..)")
public void logArgs3(String arg) {
	log.info("[logArgs3] arg={}", arg);
}
```

### 프록시 기술과 한계 - CGLIB

**CGLLB 구체 클래스 기반 프록시 문제점**

- 대상 클래스에 기본 생성자 필수
    - CGLIB는 구체 클래스를 상속 받는다. 자바 언어에서 상속을 받으면 자식 클래스의 생성자를 호출할 때 자식 클래스의 생성자에서 부모 클래스의 생성자도 호출해야 한다. (이 부분이 생략되어 있다면 자식 클래스의 생성자 첫줄에 부모 클래스의 기본 생성자를 호출하는 `super()` 가 자동으로 들어간다.) 이 부분은 자바 문법 규약이다.
    - CGLIB를 사용할 때 CGLIB가 만드는 프록시의 생성자는 우리가 호출하는 것이 아니다. CGLIB 프록시는 대상 클래스를 상속 받고, 생성자에서 대상 클래스의 기본 생성자를 호출한다. 따라서 대상 클래스의 기본 생성자를 만들어야 한다
- 생성자 2번 호출 문제
    - CGLIB는 구체 클래스를 상속 받는다. 자바 언어에서 상속을 받으면 자식 클래스의 생성자를 호출할 때 부모 클래스의 생성자도 호출해야 한다
        - 실제 target의 객체를 생성할 때 1번
        - 프록시 객체를 생성할 때 부모 클래스의 생성자 호출 1번
        
- final 키워드 클래스, 메서드 사용 불가
    - final 키워드가 클래스에 있으면 상속에 불가능하고, 메서드에 있으면 오버라이딩이 불가능하다. CGLIB는 상속을 기반으로 하기 때문에 두 경우 프록시가 생성되지 않거나 정상 동작하지 않는다

### 스프링의 기술 선택 변화

- **스프링 3.2, CGLIB를 스프링 내부에 함께 패키징**
    - CGLIB를 사용하려면 CGLIB 라이브러리가 별도로 필요했다. 스프링은 CGLIB 라이브러리를 스프링 내부에 함께 패키징해서 별도의 라이브러리 추가 없이 CGLIB를 사용할 수 있게 되었다.
    - `CGLIB spring-core org.springframework`
- **CGLIB 기본 생성자 필수 문제 해결**
    - 스프링 4.0부터 CGLIB의 기본 생성자가 필수인 문제가 해결되었다.
    - `objenesis` 라는 특별한 라이브러리를 사용해서 기본 생성자 없이 객체 생성이 가능하다.
    - 참고로 이 라이브러리는 생성자 호출 없이 객체를 생성할 수 있게 해준다.
- **생성자 2번 호출 문제**
    - 스프링 4.0부터 CGLIB의 생성자 2번 호출 문제가 해결되었다.
    - 이것도 역시 `objenesis` 라는 특별한 라이브러리 덕분에 가능해졌다.
    - 이제 생성자가 1번만 호출된다.
- **스프링 부트 2.0 - CGLIB 기본 사용**
    - 스프링 부트 2.0 버전부터 CGLIB를 기본으로 사용하도록 했다.
    - 이렇게 해서 구체 클래스 타입으로 의존관계를 주입하는 문제를 해결했다.
    - 스프링 부트는 별도의 설정이 없다면 AOP를 적용할 때 기본적으로 `proxyTargetClass=true` 로 설정해서 사용한다. 따라서 인터페이스가 있어도 JDK 동적 프록시를 사용하는 것이 아니라 항상 CGLIB를 사용해서 구체클래스를 기반으로 프록시를 생성한다.
    - 물론 스프링은 우리에게 선택권을 열어주기 때문에 다음과 깉이 설정하면 JDK 동적 프록시도 사용할 수 있다.

```
spring.aop.proxy-target-class=false
```

> 정리 : 스프링은 최종적으로 스프링 부트 2.0에서 CGLIB를 기본으로 사용하도록 결정했다. CGLIB를 사용하면, JDK 동적 프록시에서 동작하지 않는 구체 클래스 주입이 가능하다. 여기에 추가로 CGLIB의 단점들이 이제는 많이 해결되었다. `final` 키워드의 문제는 잘 사용하지 않으므로 이 부분은 크게 문제가 되지 않는다.
