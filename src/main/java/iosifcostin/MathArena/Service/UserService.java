package iosifcostin.MathArena.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import iosifcostin.MathArena.Repository.UserRepository;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.Role;
import iosifcostin.MathArena.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class UserService {

    private UserRepository userRepository;
    private MathProblemService mathProblemService;
    private RoleService roleService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private AmazonS3 s3client;


    public UserService(UserRepository userRepository, MathProblemService mathProblemService, RoleService roleService, OAuth2AuthorizedClientService authorizedClientService, AmazonS3 s3client) {
        this.userRepository = userRepository;
        this.mathProblemService = mathProblemService;
        this.roleService = roleService;
        this.authorizedClientService = authorizedClientService;
        this.s3client = s3client;
    }

    //
    @Cacheable(value = "cache.allUsers")
    public List<User> findAll(){return userRepository.findAll();}


    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByClientregId(String id) {
        return userRepository.findByClientRegistrationId(id);
    }

    public User findByClientAuthId(String gAuthId) {
        return userRepository.findByClientAuthId(gAuthId);
    }

    public Page<User> findByIdPageable(Long id, Pageable pageRequest){
        Optional<User> user = userRepository.findById(id);
        List<User> users = user.map(Collections::singletonList).orElse(Collections.emptyList());
        return new PageImpl<>(users, pageRequest, users.size());
    }

    @Cacheable(value = "cache.allUsersPageable")
    public Page<User> findAllPageable(Pageable pageable) {
        return userRepository.findAll(pageable);


    }
    @Cacheable(value = "cache.byFirstNameContaining")
    public Page<User> findByFirstNameContaining(String username, Pageable pageable) {
        return userRepository.findByFirstNameContainingOrderByIdAsc(username, pageable);
    }

    @Cacheable(value = "cache.byLastNameContaining")
    public Page<User> findByLastNameContaining(String username, Pageable pageable) {
        return userRepository.findByLastNameContainingOrderByIdAsc(username, pageable);
    }

    public User findByIdEagerly (Long id){
        return userRepository.findByIdEagerly(id);
    }

    @Cacheable(value = "cache.byEmailContaining")
    public Page<User> findByEmailContaining(String email, Pageable pageable) {
        return userRepository.findByEmailContainingOrderByIdAsc(email, pageable);
    }
    @Cacheable(value = "cache.userById", key = "#id", unless="#result == null")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByEmailEagerly(String email) {
        return userRepository.findByEmailEagerly(email);
    }
    public User findByEmailJoinFetchProblems(String email) {
        return userRepository.findByEmailJoinFetchProblems(email);
    }

    @CacheEvict(value = {"cache.allUsers","cache.allUsersPageable","cache.byEmailContaining",
            "cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void setProfilePicture (Long id ,String path){userRepository.setProfilePicture(id,path);}


    @Transactional
    @CacheEvict(value = {"cache.allUsers"
//            , "cache.allProblemsPageable"
            ,"cache.allUsersPageable","cache.byEmailContaining",
            "cache.byEmailContaining","cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void save(User user) {
        userRepository.save(user);
    }


    @CacheEvict(value = {"cache.allUsers"
//            , "cache.allProblemsPageable"
            ,"cache.allUsersPageable","cache.byEmailContaining",
            "cache.byEmailContaining","cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void setProblems(User user) {
        userRepository.setProblems(user);
    }


    @CacheEvict(value = {"cache.allUsers"
//            ,"cache.allProblemsPageable"
            , "cache.allUsersPageable"
            ,"cache.byEmailContaining",
            "cache.byEmailContaining","cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }


    public void uploadBufferedImageToServer(BufferedImage image, String fileName, String oldPicture) {

//        final String bucketName = "matharena";

        // for heroku
        String bucketName = System.getenv("S3_BUCKET_NAME");

        if (oldPicture != null) {
            s3client.deleteObject(bucketName, oldPicture.replace("https://matharena.s3.eu-central-1.amazonaws.com/", ""));
        }

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = outstream.toByteArray();
        InputStream is = new ByteArrayInputStream(buffer);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/" + "png");
        meta.setContentLength(buffer.length);

        s3client.putObject(new PutObjectRequest(bucketName, fileName, is, meta).withCannedAcl(CannedAccessControlList.PublicRead));
    }


    public List<MathProblem> getAssignedProblemsList(User user) {
        Map<Long, MathProblem> assignedProblemMap = new HashMap<>();

        List<MathProblem> problems = user.getMathProblems();
        for (MathProblem mp : problems) {
            assignedProblemMap.put(mp.getId(), mp);
        }

        List<MathProblem> userProblems = new ArrayList<>();
        List<MathProblem> allproblems = mathProblemService.findAll();
        for (MathProblem m : allproblems) {
            if (assignedProblemMap.containsKey(m.getId())) {
                userProblems.add(m);
            } else {
                userProblems.add(null);
            }
        }
        return userProblems;
    }

    public List<Role> setUserRole (){
        return Collections.singletonList(roleService.findByName("ROLE_USER"));
    }


    public List<Role> getAssignedRolesList(User user) {
        Map<Long, Role> assignedRoleMap = new HashMap<>();
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            assignedRoleMap.put(role.getId(), role);
        }

        List<Role> userRoles = new ArrayList<>();
        List<Role> allRoles = roleService.findAll();
        for (Role role : allRoles) {
            if (assignedRoleMap.containsKey(role.getId())) {
                userRoles.add(role);
            } else {
                userRoles.add(null);
            }
        }
        return userRoles;
    }

    public boolean isOauth (Authentication authentication){
        OAuth2AuthorizedClient authorizedClient = authorizedClient((OAuth2AuthenticationToken) authentication);

        return authorizedClient.getClientRegistration().getRegistrationId().equals("google") || authorizedClient.getClientRegistration().getRegistrationId().equals("facebook");
    }

    public User getUserAttributes(OAuth2AuthenticationToken auth) {
        OAuth2AuthorizedClient authorizedClient = authorizedClient(auth);
        Map userAttributes = Collections.emptyMap();
        User user = new User();
        String userInfoEndpointUri = authorizedClient.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {    // userInfoEndpointUri is optional for OIDC Clients
            userAttributes = WebClient.builder()
                    .filter(oauth2Credentials(authorizedClient))
                    .build()
                    .get()
                    .uri(userInfoEndpointUri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        }

        if (authorizedClient.getClientRegistration().getRegistrationId().equals("google") && userAttributes != null) {
            user.setFirstName((String) userAttributes.get("given_name"));
            user.setLastName((String) userAttributes.get("family_name"));
            user.setClientAuthId(auth.getName());
            user.setEmail((String) userAttributes.get("email"));
            user.setEnabled(true);
            user.setProfilePicturePath((String) userAttributes.get("picture"));
            user.setClientRegistrationId(authorizedClient.getClientRegistration().getRegistrationId());
        }
        else if (authorizedClient.getClientRegistration().getRegistrationId().equals("facebook") && userAttributes != null) {
            user.setName((String) userAttributes.get("name"));
            user.setClientAuthId(auth.getName());
            user.setEmail((String) userAttributes.get("email"));
            user.setEnabled(true);
            user.setProfilePicturePath("http://graph.facebook.com/"+userAttributes.get("id")+"/picture");
            user.setClientRegistrationId(authorizedClient.getClientRegistration().getRegistrationId());
        }
        return user;
    }

    private OAuth2AuthorizedClient authorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

    }

    private ExchangeFilterFunction oauth2Credentials(OAuth2AuthorizedClient authorizedClient) {
        return ExchangeFilterFunction.ofRequestProcessor(
                clientRequest -> {
                    ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                            .build();
                    return Mono.just(authorizedRequest);
                });
    }

}
