package com.xdtech.component;

import com.xdtech.ComponentStoreroomApplication;
import com.xdtech.component.storeroom.entity.*;
import com.xdtech.component.storeroom.repository.InWareRepository;
import com.xdtech.component.storeroom.repository.OutWareRepository;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentStoreroomApplication.class)
public class ComponentStoreroomApplicationTests {

	@Autowired
	private InWareService inWareService;

	@Autowired
	private OutWareService outWareService;

	@Autowired
	private InWareRepository inWareRepository;

	@Autowired
	private OutWareRepository outWareRepository;

	@Autowired
	private ShelvesService shelvesService;

	@Autowired
	private ZoneShelvesRepository zoneShelvesRepository;

	@Autowired
	private StorageRepository storageRepository;

	@Autowired
	private MoveWareService moveWareService;

	@Test
	public void contextLoads() {
		//System.out.println(inWareService.generateWarenum());
	}

	@Test
	public void initShelves(){

	}

	@Test
	public void search(){
//
//		ZoneShelves s = new ZoneShelves();
//		s.setCity("01");
//		s.setUnit("01");
//		s.setRoom("01");
//		s.setZone("01");
//		list = shelvesService.findZoneDetails(s);
//
//		list = shelvesService.findRooms();
//
//
//		list = shelvesService.findZoneByRoom(s);

		//InWare i = inWareRepository.findOne("2c9ae73162f00be50162f00befdb0000");
//		InWare i = inWareRepository.findWithStorageByInid("2c9ae73162f00be50162f00befdb0000");

//		Storage s = storageRepository.findWithInWareByStid("2c9ae73162f00be50162f00bf03c0001");

//		List<Storage> s = storageRepository.findAll();

		System.out.println(1);
	}

	@Test
	public void delete(){
		String ids = "2c9ae73162e6bbb60162e6bbbb7b0000,2c9ae73162e6bbb60162e6bbbb8b0001,2c9ae73162e6bbb60162e6bbbb8b0002";
//		shelvesService.delete(ids);
	}

	@Test
	public void inware(){
		ZoneShelves sh = new ZoneShelves();
		sh.setShid("2c9ae73162f1000c0162f1003d7f0003");

		String ids = "2c991721621d321101621e0a43080004,2c9ae73160246a2801602475acdd0004,2c9ae731604f4ac201604f69a8020024,2c9ae73162a8388c0162a89163fd0003";

		InWare iw = new InWare();
		iw.setWaretype("测试入库");
		iw.setWareuser("test张");
		iw.setDescription("备注一些东西");
		if(iw.getStorages() == null){
			iw.setStorages(new HashSet<Storage>());
		}
		for(String id : ids.split(",")){
			Storage st = new Storage();
			st.setEntry(id);
			st.setZoneShelves(sh);
			st.setStorestatus(Storage.STATUS_IN);
			iw.getStorages().add(st);
		}
		inWareService.save(iw);
	}

	@Test
	public void returnware(){
		OutWare out = outWareRepository.findWithStorageByOutid("2c9ae73162f731a90162f731da3b0000");
		InWare iw = new InWare();
		iw.setWaretype("归还入库");
		iw.setWareuser("test张");
		iw.setDescription("备注这是归还的东西");
		if(iw.getStorages() == null){
			iw.setStorages(new HashSet<Storage>());
		}
		for(Storage s : out.getStorages()){
			s.setStorestatus(Storage.STATUS_IN);
			iw.getStorages().add(s);
		}
		inWareService.save(iw);
	}

	@Test
	public void delinware(){
		inWareService.delete("2c9ae73162f6bd8d0162f6bd92fa0000,2c9ae73162f6c0470162f6c04cc40000,2c9ae73162f6f2f10162f6f2f7010000");
	}

	@Test
	public void outware(){
		String ids = "2c9ae73162f730dc0162f730e19c0002,2c9ae73162f730dc0162f730e19c0003";
		OutWare outWare = new OutWare();
		outWare.setWaretype("测试出库");
		outWare.setWareuser("test王");
		outWare.setDescription("就是备注");
		if(outWare.getStorages() == null){
			outWare.setStorages(new HashSet<Storage>());
		}
		for(String id : ids.split(",")){
			Storage st = storageRepository.findOne(id);//new Storage();
			st.setStorestatus(Storage.STATUS_OUT);
			outWare.getStorages().add(st);
		}
		OutWare o =  outWareService.save(outWare);
	}

	@Test
	public void deloutware(){
		outWareService.delete("2c9ae73162f1d2240162f1d22b140000");

	}

	@Test
	public void inwaresearch(){
//		Page<InWare> iw = inWareService.findAll(0,1);
//		System.out.println(iw);

		Set<Storage> s = inWareService.findStorageByInware("2c9ae73162fa54c60162fa54d7e70000");
		System.out.println(s);
	}

	@Test
	public void moveware(){

		MoveWare moveWare = new MoveWare();
		moveWare.setWareuser("test李");
		moveWare.setDescription("移库测试");
		if(moveWare.getStorages() == null){
			moveWare.setStorages(new HashSet<Storage>());
		}

		String stids = "2c9ae73162fa55250162fa552ae20001,2c9ae73162fa55250162fa552ae40003";
		List<Storage> list = storageRepository.findByStidIn(stids.split(","));
		moveWare.getStorages().addAll(list);

		ZoneShelves target = zoneShelvesRepository.findOne("2c9ae73162f1000c0162f1003d710000");
		moveWareService.save(moveWare, target);

	}

	@Test
	public void movebatch(){
		ZoneShelves source = zoneShelvesRepository.findOne("2c9ae73162f1000c0162f1003d7f0002");
		ZoneShelves target = zoneShelvesRepository.findOne("2c9ae73162f1000c0162f1003d7e0001");

		MoveWare moveWare = new MoveWare();
		moveWare.setWareuser("test只");
		moveWare.setDescription("备注一些东西移库");
		List<ZoneShelves> sources = new ArrayList<ZoneShelves>();
		sources.add(source);
		List<ZoneShelves> targets = new ArrayList<ZoneShelves>();
		targets.add(target);
		//moveWareService.save(moveWare, sources, targets);
	}

}
