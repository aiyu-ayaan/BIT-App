/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
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