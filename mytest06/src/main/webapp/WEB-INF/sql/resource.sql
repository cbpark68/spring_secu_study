
/************DB 이용해서 자원 접근 권한 설정을 위한 테이블 만들기****************/

--사용자 테이블
CREATE TABLE TB_USERS (
	USERNAME VARCHAR2(50) NOT NULL,
	PASSWORD VARCHAR2(50) NOT NULL,
	ENABLED INTEGER NOT NULL,
	CONSTRAINT PK_USERS PRIMARY KEY(USERNAME)
);

--롤 테이블
CREATE TABLE TB_ROLES (
	AUTHORITY VARCHAR2(50) NOT NULL,
	ROLE_NAME VARCHAR2(100),
	DESCRIPTION VARCHAR2(100),
	CREATE_DATE DATE,
	MODIFY_DATE DATE,
	CONSTRAINT PK_ROLES PRIMARY KEY(AUTHORITY)
);

--권한 테이블 관계
CREATE TABLE TB_AUTHORITIES (
	USERNAME VARCHAR2(50) NOT NULL,
	AUTHORITY VARCHAR2(50) NOT NULL,
	CONSTRAINT PK_AUTHORITIES PRIMARY KEY(USERNAME,AUTHORITY),
	CONSTRAINT FK_USERS FOREIGN KEY(USERNAME) REFERENCES TB_USERS(USERNAME),
	CONSTRAINT FK_ROLES3 FOREIGN KEY(AUTHORITY) REFERENCES TB_ROLES(AUTHORITY)
);


--롤 계층 테이블
CREATE TABLE TB_ROLES_HIERARCHY (
	PARENT_ROLE VARCHAR2(50) NOT NULL,
	CHILD_ROLE VARCHAR2(50) NOT NULL,
	CONSTRAINT PK_ROLES_HIERARCHY PRIMARY KEY(PARENT_ROLE,CHILD_ROLE),
	CONSTRAINT FK_ROLES1 FOREIGN KEY(PARENT_ROLE) REFERENCES TB_ROLES(AUTHORITY),
	CONSTRAINT FK_ROLES2 FOREIGN KEY(CHILD_ROLE) REFERENCES TB_ROLES (AUTHORITY)
);


--보호된 자원 테이블
CREATE TABLE TB_SECURED_RESOURCES (
	RESOURCE_ID VARCHAR2(50) NOT NULL,
	RESOURCE_NAME VARCHAR2(50),
	RESOURCE_PATTERN VARCHAR2(300) NOT NULL,
	DESCRIPTION VARCHAR2(100),
	RESOURCE_TYPE VARCHAR2(10),
	SORT_ORDER VARCHAR2(10),
	CREATE_DATE DATE,
	MODIFY_DATE DATE,
	CONSTRAINT PK_RECURED_RESOURCES PRIMARY KEY(RESOURCE_ID)
);

-- 보호된 자원-롤 관계 테이블
CREATE TABLE TB_SECURED_RESOURCES_ROLE (
	RESOURCE_ID VARCHAR(50) NOT NULL,
	AUTHORITY VARCHAR(50) NOT NULL,
    CREATE_DATE DATE,
	CONSTRAINT PK_SECURED_RESOURCES_ROLE PRIMARY KEY(RESOURCE_ID,AUTHORITY)
   ,CONSTRAINT FK_SECURED_RESOURCES FOREIGN KEY(RESOURCE_ID) REFERENCES TB_SECURED_RESOURCES(RESOURCE_ID)
   ,CONSTRAINT FK_ROLES4 FOREIGN KEY (AUTHORITY) REFERENCES TB_ROLES(AUTHORITY)
);

/***********각 테이블에 실습 데이터 추가하기   ******************/
--사용자 정보
INSERT INTO TB_USERS (username, password, enabled) VALUES ('user', '1111', 1);
INSERT INTO TB_USERS (username, password, enabled) VALUES ('admin', '1111', 1);


-- 롤정보
INSERT INTO TB_ROLES VALUES ('ROLE_ANONYMOUS'               ,'모든 사용자'      , '',SYSDATE, SYSDATE);
INSERT INTO TB_ROLES VALUES ('IS_AUTHENTICATED_ANONYMOUSLY' ,'스프링시큐리티 내부사용(롤부여 금지)'      , '',SYSDATE, SYSDATE);
INSERT INTO TB_ROLES VALUES ('IS_AUTHENTICATED_REMEMBERED'  ,'스프링시큐리티 내부사용(롤부여 금지)', '', SYSDATE,SYSDATE);
INSERT INTO TB_ROLES VALUES ('IS_AUTHENTICATED_FULLY'       ,'스프링시큐리티 내부사용(롤부여 금지)'    , '',SYSDATE, SYSDATE);
INSERT INTO TB_ROLES VALUES ('ROLE_USER'                    ,'일반 사용자'      , '', SYSDATE, SYSDATE);
INSERT INTO TB_ROLES VALUES ('ROLE_ADMIN'                   ,'관리자'           , '',SYSDATE, SYSDATE);

-- 회원 권한 입력
INSERT INTO TB_AUTHORITIES (username, authority) VALUES ('user', 'ROLE_USER');
INSERT INTO TB_AUTHORITIES (username, authority) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO TB_AUTHORITIES (username, authority) VALUES ('admin', 'ROLE_USER');


-- 롤 계층구조
INSERT INTO TB_ROLES_HIERARCHY VALUES ('ROLE_ANONYMOUS'               ,'IS_AUTHENTICATED_ANONYMOUSLY');
INSERT INTO TB_ROLES_HIERARCHY VALUES ('IS_AUTHENTICATED_ANONYMOUSLY' ,'IS_AUTHENTICATED_REMEMBERED');
INSERT INTO TB_ROLES_HIERARCHY VALUES ('IS_AUTHENTICATED_REMEMBERED'  ,'IS_AUTHENTICATED_FULLY');
INSERT INTO TB_ROLES_HIERARCHY VALUES ('IS_AUTHENTICATED_FULLY'       ,'ROLE_USER');
INSERT INTO TB_ROLES_HIERARCHY VALUES ('ROLE_USER'                    ,'ROLE_ADMIN');




-- 보호된 자원 등록
INSERT INTO TB_SECURED_RESOURCES  VALUES('web-000001', '소개 페이지', '/intro/intro.do', '소개 페이지를 위한 롤', 'url', '1', SYSDATE, SYSDATE);
INSERT INTO TB_SECURED_RESOURCES  VALUES('web-000002', '관리자 페이지', '/admin/adminMain.do', '관리자 페이지 접근 제한 롤', 'url', '1', SYSDATE, SYSDATE);
INSERT INTO TB_SECURED_RESOURCES  VALUES('web-000003', '관리자 요청', '/admin/**', '관리자 페이지 접근 제한 롤', 'url', '2', SYSDATE, SYSDATE);

-- 권한 롤 매핑
INSERT INTO TB_SECURED_RESOURCES_ROLE(AUTHORITY,RESOURCE_ID,CREATE_DATE)  VALUES ('ROLE_USER', 'web-000001',SYSDATE);
INSERT INTO TB_SECURED_RESOURCES_ROLE(AUTHORITY,RESOURCE_ID,CREATE_DATE) VALUES ('ROLE_ADMIN', 'web-000002', SYSDATE);
commit;

/******
-- url 형식인 보호자원 - Role 맾핑 정보를 조회하는 defualt 쿼리이다.
SELECT A.RESOURCE_PATTERN AS URL, B.AUTHORITY AS AUTHORITY
FROM TB_SECURED_RESOURCES A, TB_SECURED_RESOURCES_ROLE B
WHERE A.RESOURCE_ID = B.RESOURCE_ID
AND A.RESOURCE_TYPE = 'url'
ORDER BY A.SORT_ORDER;


--매 reqeust마다 best matching url 보호자원 - Role 매핑 정보를
--얻기 위한 default 쿼리입니다.
SELECT A.RESOURCE_PATTERN URI, B.AUTHORITY AUTHORITY 
FROM TB_SECURED_RESOURCES A, TB_SECURED_RESOURCES_ROLE B
WHERE A.RESOURCE_ID = B.RESOURCE_ID
AND A.RESOURCE_ID =  (SELECT RESOURCE_ID FROM 
                       (SELECT RESOURCE_ID, ROW_NUMBER() OVER (ORDER BY SORT_ORDER) RESOURCE_ORDER
                        FROM TB_SECURED_RESOURCES C
                        WHERE REGEXP_LIKE(:URL, C.RESOURCE_PATTERN)
                        AND C.RESOURCE_TYPE = 'url'
                        ORDER BY C.SORT_ORDER)
                      WHERE RESOURCE_ORDER = 1 );
                      
--ROLE의 계층(Hierachy) 관계를 조회하는 default 쿼리이다.
SELECT A.CHILD_ROLE CHILD, A.PARENT_ROLE PARENT
FROM TB_ROLES_HIERARCHY A LEFT JOIN TB_ROLES_HIERARCHY B ON (A.CHILD_ROLE = B.PARENT_ROLE);
*******/
SELECT A.CHILD_ROLE CHILD, A.PARENT_ROLE PARENT
FROM TB_ROLES_HIERARCHY A LEFT JOIN TB_ROLES_HIERARCHY B ON (A.CHILD_ROLE = B.PARENT_ROLE);
