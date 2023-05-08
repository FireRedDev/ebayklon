import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './offer.reducer';

export const OfferDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const offerEntity = useAppSelector(state => state.offer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="offerDetailsHeading">Offer</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{offerEntity.id}</dd>
          <dt>
            <span id="offerValue">Offer Value</span>
          </dt>
          <dd>{offerEntity.offerValue}</dd>
          <dt>Offer Name</dt>
          <dd>{offerEntity.offerName ? offerEntity.offerName.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/offer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Zurück</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/offer/${offerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Bearbeiten</span>
        </Button>
      </Col>
    </Row>
  );
};

export default OfferDetail;
