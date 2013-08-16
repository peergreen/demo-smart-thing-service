/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.demo.smartthing.service;

import java.util.Collection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.peergreen.demo.smartthing.jpa.ChannelEntity;
import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;

public class JPAPersistenceService implements PersistenceService {

    private final UserTransaction userTransaction;


    private final EntityManager entityManager;


    public JPAPersistenceService(EntityManager entityManager) {
        try {
            this.userTransaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        } catch (NamingException e) {
            throw new IllegalStateException("Not able to find user transaction", e);
        }
        this.entityManager = entityManager;
    }


    private void begin() {
        try {
            userTransaction.begin();
        } catch (NotSupportedException | SystemException e) {
            e.printStackTrace();
        }
    }


    private void commit() {
        try {
            userTransaction.commit();
        } catch ( SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Collection<DeviceEntity> listDevices() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DeviceEntity> query = cb.createQuery(DeviceEntity.class);

        Root<DeviceEntity> deviceEntity = query.from(DeviceEntity.class);
        TypedQuery<DeviceEntity> emQuery = entityManager.createQuery(query.select(deviceEntity));
        return emQuery.getResultList();
    }

    @Override
    public synchronized DeviceEntity findDeviceByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DeviceEntity> query = cb.createQuery(DeviceEntity.class);

        Root<DeviceEntity> deviceEntity = query.from(DeviceEntity.class);
        query.where(cb.equal(deviceEntity.get("name"), cb.parameter(String.class, "name")));
        TypedQuery<DeviceEntity> emQuery = entityManager.createQuery(query.select(deviceEntity));
        emQuery.setParameter("name", name);
        try {
            return emQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public synchronized void addDevice(DeviceEntity deviceEntity) {
        try {
            begin();
            entityManager.persist(deviceEntity);
        } finally {
            commit();
        }
    }


    @Override
    public synchronized void addSensor(SensorEntity sensorEntity) {
        try {
            begin();
            entityManager.persist(sensorEntity);
            DeviceEntity deviceEntity = sensorEntity.getDeviceEntity();
            deviceEntity.getSensors().add(sensorEntity);
            entityManager.merge(deviceEntity);
        } finally {
            commit();
        }

    }


    @Override
    public synchronized void addChannel(ChannelEntity channelEntity) {
        try {
            begin();
            entityManager.persist(channelEntity);
            channelEntity.getSensor().getChannels().add(channelEntity);
            entityManager.merge(channelEntity.getSensor());

        } finally {
            commit();
        }
    }

    @Override
    public synchronized void updateChannel(ChannelEntity channelEntity) {
        try {
            begin();
            entityManager.merge(channelEntity);
        } finally {
            commit();
        }
    }


    @Override
    public synchronized SensorEntity findSensor(String deviceName, String sensorName) {
        Query query = entityManager.createQuery("SELECT s FROM SensorEntity s JOIN s.deviceEntity de WHERE de.name = :deviceName and s.name = :sensorName");
        query.setParameter("deviceName", deviceName);
        query.setParameter("sensorName", sensorName);
        try {
            return (SensorEntity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public synchronized ChannelEntity findChannel(String deviceName, String sensorName, long id) {
        Query query = entityManager.createQuery("SELECT c FROM ChannelEntity c JOIN c.sensorEntity se JOIN se.deviceEntity de WHERE de.name = :deviceName and se.name = :sensorName and c.id = :id");
        query.setParameter("deviceName", deviceName);
        query.setParameter("sensorName", sensorName);
        query.setParameter("id", id);
        try {
            return (ChannelEntity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
