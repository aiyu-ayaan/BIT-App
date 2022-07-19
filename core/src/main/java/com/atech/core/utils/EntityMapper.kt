/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.utils


/**
 * Mapper class to convert one data to another
 *
 * @author Ayaan
 * @since 4.0
 */
interface EntityMapper<Entity, DomainModel> {

    fun mapFormEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity

}