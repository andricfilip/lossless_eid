package pmf.kg.eid_api.entity;

import com.andric.EidCard;
import com.andric.EidInfo;
import jakarta.persistence.*;
import lombok.*;

import javax.imageio.ImageIO;
import javax.smartcardio.CardException;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@Table(name = "eid_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EidInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personal_number", nullable = false, unique = true)
    private String personalNumber;

    @Column(name = "surname")
    private String surname;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "parent_given_name")
    private String parentGivenName;

    @Column(name = "sex")
    private String sex;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "community_of_birth")
    private String communityOfBirth;

    @Column(name = "state_of_birth")
    private String stateOfBirth;

    @Column(name = "doc_reg_no")
    private String docRegNo;

    @Column(name = "issuing_date")
    private String issuingDate;

    @Column(name = "expiry_date")
    private String expiryDate;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "state")
    private String state;

    @Column(name = "community")
    private String community;

    @Column(name = "place")
    private String place;

    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "house_letter")
    private String houseLetter;

    @Column(name = "entrance")
    private String entrance;

    @Column(name = "floor")
    private String floor;

    @Column(name = "appartment_number")
    private String appartmentNumber;

    @Column(name = "address_date")
    private String addressDate;

    @Lob
    @Basic(fetch = FetchType.LAZY) // bolje da ne vuče sliku svaki put
    @Column(name = "photo")
    private byte[] photo;

    public EidInfoEntity(EidCard card) throws CardException, IOException {
            EidInfo eidInfo = card.readEidInfo();
            this.personalNumber = eidInfo.getPersonalNumber();
            this.surname = eidInfo.getSurname();
            this.givenName = eidInfo.getGivenName();
            this.parentGivenName = eidInfo.getParentGivenName();
            this.sex = eidInfo.getSex();
            this.dateOfBirth = eidInfo.getDateOfBirth();
            this.placeOfBirth = eidInfo.getPlaceOfBirth();
            this.communityOfBirth = eidInfo.getCommunityOfBirth();
            this.stateOfBirth = eidInfo.getStateOfBirth();
            this.docRegNo = eidInfo.getDocRegNo();
            this.issuingDate = eidInfo.getIssuingDate();
            this.expiryDate = eidInfo.getExpiryDate();
            this.issuingAuthority = eidInfo.getIssuingAuthority();
            this.state = eidInfo.getState();
            this.community = eidInfo.getCommunity();
            this.place = eidInfo.getPlace();
            this.street = eidInfo.getStreet();
            this.houseNumber = eidInfo.getHouseNumber();
            this.houseLetter = eidInfo.getHouseLetter();
            this.entrance = eidInfo.getEntrance();
            this.floor = eidInfo.getFloor();
            this.appartmentNumber = eidInfo.getAppartmentNumber();
            this.addressDate = eidInfo.getAddressDate();
            Image photo = card.readEidPhoto();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage) photo, "jpg", baos);
            this.photo=baos.toByteArray();



    }
}
