import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAuction } from 'app/shared/model/auction.model';
import { getEntities as getAuctions } from 'app/entities/auction/auction.reducer';
import { IOffer } from 'app/shared/model/offer.model';
import { getEntity, updateEntity, createEntity, reset } from './offer.reducer';

export const OfferUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const auctions = useAppSelector(state => state.auction.entities);
  const offerEntity = useAppSelector(state => state.offer.entity);
  const loading = useAppSelector(state => state.offer.loading);
  const updating = useAppSelector(state => state.offer.updating);
  const updateSuccess = useAppSelector(state => state.offer.updateSuccess);

  const handleClose = () => {
    navigate('/offer');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAuctions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...offerEntity,
      ...values,
      offerName: auctions.find(it => it.id.toString() === values.offerName.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...offerEntity,
          offerName: offerEntity?.offerName?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="ebayklonApp.offer.home.createOrEditLabel" data-cy="OfferCreateUpdateHeading">
            Offer erstellen oder bearbeiten
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="offer-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Offer Value" id="offer-offerValue" name="offerValue" data-cy="offerValue" type="text" />
              <ValidatedField id="offer-offerName" name="offerName" data-cy="offerName" label="Offer Name" type="select">
                <option value="" key="0" />
                {auctions
                  ? auctions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/offer" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Zurück</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Speichern
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default OfferUpdate;
